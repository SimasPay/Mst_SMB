using System;
using System.IO;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.Xml.Serialization;



namespace FIXMLCodeGenerator
{
	class CodeGenerator
	{
		public static StreamWriter CPPFile;
		public static StreamWriter HFile;
		public static StreamWriter JavaFile;
		public static StreamWriter JSFile;
		public static XmlNode HeaderNode;
		public static XmlNode TrailerNode;
		public static XmlNode FixTop;
        public static XmlNode EncryptTop;
        public static XmlNode DbNode;
		public static bool bMsgTypeIsNumeric = false;
		public static int LastMsgType = 0;
		static string DefaultBeginString;
		static Dictionary<string, string> TypedefsTable = new Dictionary<string, string>();
		static Dictionary<string, string> DefinesTable = new Dictionary<string, string>();
		static Dictionary<string, string> JavaDefinesTable = new Dictionary<string, string>();
		public static String AllJSFieldsNames = "";
		public static String CurrentJSScope = "";
		public static String CurrentJSShortScope = "";
		public static String TopMostJSScope = "";
		public static String JavaDir = "";
		public static String JavaPrefix = "";
        public static String DbType = "mysql";
        private static readonly char[] SpecialChars = "[]~-+\\|';:,<\">./?!@#$%^&*()".ToCharArray();

		public enum Language { CPP, Java, JavaScript, Sql };

		static String GetNodeID(XmlNode N)
		{
			if (N.Attributes.GetNamedItem("id") != null)
			{
				return N.Attributes.GetNamedItem("id").Value;
			}
			return N.Attributes.GetNamedItem("name").Value;
		}


		public static void DumpRequiredFields(XmlNode Class, Language Lang)
		{
			int RequiredCount = 0;
			foreach (XmlNode Field in Class.SelectNodes("./field|./group"))
			{
				if (FieldExistsInSuperClass(Class, Field))
					continue;

				if (Field.Attributes.GetNamedItem("required").Value == "Y")
				{
					RequiredCount++;
				}
			}
			if (RequiredCount == 0)
				return;
			if (Lang == Language.CPP)
			{
				CPPFile.WriteLine("m_RequiredFieldsCount\t=\t" + RequiredCount.ToString() + ";");
				CPPFile.WriteLine("m_pRequiredFields\t=\tnew\tvoid\t**[m_RequiredFieldsCount+1];");
			}
			if (Lang == Language.Java)
			{
				JavaFile.WriteLine("m_RequiredFieldsCount\t=\t" + RequiredCount + ";");
				JavaFile.WriteLine("m_pRequiredFields\t=\tnew\tObject[m_RequiredFieldsCount+1];");
			}
			if (Lang == Language.JavaScript)
			{
				JSFile.WriteLine("this.m_RequiredFieldsCount\t=\t" + RequiredCount + ";");
				JSFile.WriteLine("this.m_pRequiredFields=[];");
			}


			int RequiredIndex = 0;
			foreach (XmlNode Field in Class.SelectNodes("./field|./group"))
			{
				if (FieldExistsInSuperClass(Class, Field))
					continue;

				if (Field.Attributes.GetNamedItem("required").Value == "Y")
				{
					if (Lang == Language.CPP)
						CPPFile.WriteLine("m_pRequiredFields[" + RequiredIndex.ToString() + "]\t=\t(void\t**)m_p" + GetNodeID(Field) + ";");
					if (Lang == Language.Java)
						JavaFile.WriteLine("m_pRequiredFields[" + RequiredIndex + "]\t=\tm_p" + GetNodeID(Field) + ";");
					if (Lang == Language.JavaScript)
						JSFile.WriteLine("this.m_pRequiredFields[" + RequiredIndex + "]\t=\tthis.m_p" + GetNodeID(Field) + ";");
					RequiredIndex++;
				}
			}
			if (Lang == Language.CPP)
				CPPFile.WriteLine("m_pRequiredFields[" + RequiredIndex.ToString() + "]\t=\tNULL;");
			if (Lang == Language.Java)
				JavaFile.WriteLine("m_pRequiredFields[" + RequiredIndex + "]\t=\tnull;");
			if (Lang == Language.JavaScript)
				JSFile.WriteLine("this.m_pRequiredFields[" + RequiredIndex + "]\t=\tnull;");
		}


		static String GetFieldType(string Prefix, XmlNode Field)
		{
			string RetVal = TypedefsTable["T" + Prefix + Field.Attributes.GetNamedItem("name").Value];
			string TypeDefValue = TypedefsTable["T" + Prefix + Field.Attributes.GetNamedItem("name").Value];
			if (DefinesTable.ContainsKey(TypeDefValue))
				RetVal = DefinesTable[TypeDefValue];
			return RetVal;
		}


		public static void DumpRequiredFieldsChecker(string Prefix, XmlNode Class, Language Lang)
		{
			if (Lang == Language.CPP)
			{
				HFile.WriteLine("virtual bool CheckRequiredFields();");

				CPPFile.WriteLine("bool\t" + GenerateClassHierarchy(Prefix, Class, Lang) + "::CheckRequiredFields()");
				CPPFile.WriteLine("{");
				CPPFile.WriteLine("if(!" + BaseClassName(Prefix, Class, Lang) + "::CheckRequiredFields()) return false;");
				if (Class.Name == "message" && Class.Attributes.GetNamedItem("ExtendsMessage") == null)
				{
					CPPFile.WriteLine("if(!Header().CheckRequiredFields()) return false;");
					CPPFile.WriteLine("if(!Trailer().CheckRequiredFields()) return false;");
				}

				foreach (XmlNode Field in Class.SelectNodes("./field|./group"))
				{
					if (FieldExistsInSuperClass(Class, Field))
						continue;

					if (Field.Attributes.GetNamedItem("required").Value == "Y")
					{
						CPPFile.WriteLine("if(m_p" + GetNodeID(Field) + "[0]\t==\tNULL)\treturn\tfalse;");
					}
				}

				foreach (XmlNode Field in Class.SelectNodes("./group"))
				{
					if (FieldExistsInSuperClass(Class, Field))
						continue;
					String FieldType = GetFieldType(Prefix, Field);

					CPPFile.WriteLine("if(m_p" + GetNodeID(Field) + "[0]\t!=\tNULL\t&&\t*m_p" + GetNodeID(Field) + "[0]\t>\t0)");
					CPPFile.WriteLine("for(" + FieldType + "\tI=0;I<*m_p" + GetNodeID(Field) + "[0];I++)");
					CPPFile.WriteLine("if(!m_p" + BuildCPPGroupID(Field) + "[I]->CheckRequiredFields()) return false;");
				}
				CPPFile.WriteLine("return\ttrue;");
				CPPFile.WriteLine("}");
			}
			if (Lang == Language.Java)
			{
                JavaFile.WriteLine("@Override\tpublic\tboolean\tcheckRequiredFields()");
				JavaFile.WriteLine("{");
                JavaFile.WriteLine("boolean status = true;");
				JavaFile.WriteLine("if(!super.checkRequiredFields()) status = false;");
				if (Class.Name == "message" && Class.Attributes.GetNamedItem("ExtendsMessage") == null)
				{
					JavaFile.WriteLine("if(!header().checkRequiredFields()) status = false;");
					JavaFile.WriteLine("if(!trailer().checkRequiredFields()) status = false;");
				}
                string className = BuildClassName(Class, Lang);
                
				foreach (XmlNode Field in Class.SelectNodes("./field|./group"))
				{
					if (FieldExistsInSuperClass(Class, Field))
						continue;
					if (Field.Attributes.GetNamedItem("required").Value == "Y")
					{
						JavaFile.WriteLine("if(m_p" + GetNodeID(Field) + "\t==\tnull){");
                        JavaFile.WriteLine("\t log.info(\"MissingParam:" + GetNodeID(Field) + " class:" + className + "\");");
                        JavaFile.WriteLine("\t status = false;");
                        JavaFile.WriteLine("}");
					}
				}

				foreach (XmlNode Field in Class.SelectNodes("./group"))
				{
					if (FieldExistsInSuperClass(Class, Field))
						continue;
					String FieldType = GetFieldType(Prefix, Field);

					JavaFile.WriteLine("if(m_p" + GetNodeID(Field) + "\t!=\tnull\t&&\tm_p" + GetNodeID(Field) + "\t>\t0)");
					JavaFile.WriteLine("for(" + FieldType + "\tI=0;I<m_p" + GetNodeID(Field) + ";I++)");
					JavaFile.WriteLine("if(!m_p" + BuildJavaGroupID(Field) + "[I].checkRequiredFields()) status = false;");
				}
				JavaFile.WriteLine("return\tstatus;");
				JavaFile.WriteLine("}");
			}

			if (Lang == Language.JavaScript)
			{
				/*JSFile.WriteLine(CurrentJSScope + ".prototype.CheckRequiredFields=function()"); //commented for enhancement #2380
				JSFile.WriteLine("{");
				JSFile.WriteLine("if(!" + CurrentJSScope + ".superclass.CheckRequiredFields.call(this)){return false;}");
				if (Class.Name == "message" && Class.Attributes.GetNamedItem("ExtendsMessage") == null)
				{
					JSFile.WriteLine("if(!Header().CheckRequiredFields()){return false;}");
					JSFile.WriteLine("if(!Trailer().CheckRequiredFields()){return false;}");
				}
				foreach (XmlNode Field in Class.SelectNodes("./field|./group"))
				{
					if (FieldExistsInSuperClass(Class, Field))
						continue;
					if (Field.Attributes.GetNamedItem("required").Value == "Y")
					{
						JSFile.WriteLine("if(m_p" + GetNodeID(Field) + "\t===\tnull)\t{return\tfalse;}");
					}
				}

				foreach (XmlNode Field in Class.SelectNodes("./group"))
				{
					if (FieldExistsInSuperClass(Class, Field))
						continue;
					String FieldType = GetFieldType(Prefix, Field);

					JSFile.WriteLine("if(m_p" + GetNodeID(Field) + "\t!==\tnull\t&&\tm_p" + GetNodeID(Field) + "\t>\t0)");
					JSFile.WriteLine("{for(var\tI=0;I<m_p" + GetNodeID(Field) + ";I++)");
					JSFile.WriteLine("{if(!m_p" + BuildJSGroupID(Field) + "[I].CheckRequiredFields()){return false;}}}");
				}
				JSFile.WriteLine("return\ttrue;");
				JSFile.WriteLine("};");*/
			}
		}

		public static void DumpAccessorFunctions(string Prefix, XmlNode Class, Language Lang)
		{
			bool bRemoteModified = false;
			if (Class.Attributes.GetNamedItem("RemoteModified") != null && Class.Attributes.GetNamedItem("RemoteModified").Value == "Y")
			{
				bRemoteModified = true;
			}

			if (Class.Name == "message" && Class.Attributes.GetNamedItem("ExtendsMessage") == null)
			{
				if (Lang == Language.CPP)
				{
					HFile.WriteLine("C" + Prefix + BuildClassName(HeaderNode, Lang) + "\t&Header(){return\t*m_pHeader;}");
					HFile.WriteLine("C" + Prefix + BuildClassName(TrailerNode, Lang) + "\t&Trailer(){return\t*m_pTrailer;}");
				}
				if (Lang == Language.Java)
				{
					JavaFile.WriteLine("public\tC" + Prefix + BuildClassName(HeaderNode, Lang) + "\theader(){return\tm_pHeader;}");
					JavaFile.WriteLine("public\tC" + Prefix + BuildClassName(TrailerNode, Lang) + "\ttrailer(){return\tm_pTrailer;}");
				}
				if (Lang == Language.JavaScript)
				{
					JSFile.WriteLine(CurrentJSScope + ".prototype." + "Header=\tfunction(){return this.m_pHeader;};");
					JSFile.WriteLine(CurrentJSScope + ".prototype." + "Trailer=\tfunction(){return this.m_pTrailer;};");
				}
			}

			foreach (XmlNode Field in Class.SelectNodes("./field|./group"))
			{
				if (FieldExistsInSuperClass(Class, Field))
					continue;
				String FieldType = GetFieldType(Prefix, Field);

				if (Field.Name == "field")
				{
					if (Lang == Language.CPP)
					{
						HFile.WriteLine(FieldType + "\tGet" + GetNodeID(Field) + "Value();");
						HFile.WriteLine(FieldType + "\t&Get" + GetNodeID(Field) + "ByRef();");
						HFile.WriteLine(FieldType + "\t*Get" + GetNodeID(Field) + "Ptr();");
						HFile.WriteLine("void\tSet" + GetNodeID(Field) + "ByRef(" + FieldType + "\t&NewVal);");
						HFile.WriteLine("void\tSet" + GetNodeID(Field) + "Value(" + FieldType + "\tNewVal);");
						HFile.WriteLine("void\tSet" + GetNodeID(Field) + "Ptr(" + FieldType + "\t*pNewVal);");
						HFile.WriteLine("void\tClear" + GetNodeID(Field) + "();");
						if(bRemoteModified)
						HFile.WriteLine("bool\tIsRemoteModified" + GetNodeID(Field) + "();");
					}
				}
				else
					if (IsGroup(Field))
					{
						if (Lang == Language.CPP)
						{
							HFile.WriteLine(FieldType + "\tGet" + GetNodeID(Field) + "Value();");
							HFile.WriteLine(FieldType + "\t&Get" + GetNodeID(Field) + "ByRef();");
							HFile.WriteLine(FieldType + "\t*Get" + GetNodeID(Field) + "Ptr();");
							HFile.WriteLine("void\tClear" + BuildCPPGroupID(Field) + "();");
							HFile.WriteLine("C" + Prefix + BuildClassName(Field, Lang) + "\t**Get_" + BuildCPPGroupID(Field) + "(" + FieldType + "\t&EntriesCount);");
							HFile.WriteLine("C" + Prefix + BuildClassName(Field, Lang) + "\t**Allocate_" + BuildCPPGroupID(Field) + "(" + FieldType + "\tEntriesCount);");
						}
					}
			}

			foreach (XmlNode Field in Class.SelectNodes("./field|./group"))
			{
				if (FieldExistsInSuperClass(Class, Field))
					continue;
				String FieldType = GetFieldType(Prefix, Field);
				if (Field.Name == "field")
				{
					if (Lang == Language.CPP)
					{
						CPPFile.WriteLine(FieldType + "\t" + GenerateClassHierarchy(Prefix, Class, Lang) + "::Get" + GetNodeID(Field) + "Value()");
						CPPFile.WriteLine("{");
						CPPFile.WriteLine("return\t*m_p" + GetNodeID(Field) + "[0];");
						CPPFile.WriteLine("}");

						CPPFile.WriteLine(FieldType + "\t&" + GenerateClassHierarchy(Prefix, Class, Lang) + "::Get" + GetNodeID(Field) + "ByRef()");
						CPPFile.WriteLine("{");
						CPPFile.WriteLine("return\t*m_p" + GetNodeID(Field) + "[0];");
						CPPFile.WriteLine("}");

						CPPFile.WriteLine(FieldType + "\t*" + GenerateClassHierarchy(Prefix, Class, Lang) + "::Get" + GetNodeID(Field) + "Ptr()");
						CPPFile.WriteLine("{");
						CPPFile.WriteLine("return\tm_p" + GetNodeID(Field) + "[0];");
						CPPFile.WriteLine("}");

						CPPFile.WriteLine("void\t" + GenerateClassHierarchy(Prefix, Class, Lang) + "::Set" + GetNodeID(Field) + "ByRef(" + FieldType + "\t&NewVal)");
						CPPFile.WriteLine("{");
						CPPFile.WriteLine("if(m_p" + GetNodeID(Field) + "[0])");
						CPPFile.WriteLine("if(m_p" + GetNodeID(Field) + "[1] != m_p" + GetNodeID(Field) + "[0])");
						CPPFile.WriteLine("delete\tm_p" + GetNodeID(Field) + "[0];");
						CPPFile.WriteLine("m_p" + GetNodeID(Field) + "[0]\t=\tnew\t" + FieldType + "(NewVal);");
						CPPFile.WriteLine("}");

						CPPFile.WriteLine("void\t" + GenerateClassHierarchy(Prefix, Class, Lang) + "::Set" + GetNodeID(Field) + "Value(" + FieldType + "\tNewVal)");
						CPPFile.WriteLine("{");
						CPPFile.WriteLine("Set" + GetNodeID(Field) + "ByRef(NewVal);");
						CPPFile.WriteLine("}");


						CPPFile.WriteLine("void\t" + GenerateClassHierarchy(Prefix, Class, Lang) + "::Set" + GetNodeID(Field) + "Ptr(" + FieldType + "\t*pNewVal)");
						CPPFile.WriteLine("{");
						CPPFile.WriteLine("if(m_p" + GetNodeID(Field) + "[0])");
						CPPFile.WriteLine("if(m_p" + GetNodeID(Field) + "[1] != m_p" + GetNodeID(Field) + "[0])");
						CPPFile.WriteLine("delete\tm_p" + GetNodeID(Field) + "[0];");
						CPPFile.WriteLine("m_p" + GetNodeID(Field) + "[0]\t=\tpNewVal;");
						CPPFile.WriteLine("}");

						CPPFile.WriteLine("void\t" + GenerateClassHierarchy(Prefix, Class, Lang) + "::Clear" + GetNodeID(Field) + "()");
						CPPFile.WriteLine("{");
						CPPFile.WriteLine("Set" + GetNodeID(Field) + "Ptr(NULL);");
						CPPFile.WriteLine("}");
						if (bRemoteModified)
						{
							CPPFile.WriteLine("bool\t" + GenerateClassHierarchy(Prefix, Class, Lang) + "::IsRemoteModified" + GetNodeID(Field) + "()");
							CPPFile.WriteLine("{");
							CPPFile.WriteLine("return IsRemoteModifiedField(\"" + GetNodeID(Field) + "\");");
							CPPFile.WriteLine("}");
						}
					}
					if (Lang == Language.Java)
					{
						string accessor = "public";
						if (Field.Attributes.GetNamedItem("References") != null)
						{
							accessor = "private";
						}

						JavaFile.WriteLine(accessor + "\t" + FieldType + "\tget" + GetNodeID(Field) + "()");
						JavaFile.WriteLine("{");
						JavaFile.WriteLine("return\tm_p" + GetNodeID(Field) + ";");
						JavaFile.WriteLine("}");

						JavaFile.WriteLine(accessor + "\tvoid\tset" + GetNodeID(Field) + "(" + FieldType + "\tNewVal)");
						JavaFile.WriteLine("{");
						JavaFile.WriteLine("m_p" + GetNodeID(Field) + "\t=\tNewVal;");
						JavaFile.WriteLine("}");
						if (bRemoteModified)
						{
							JavaFile.WriteLine(accessor + "\tBoolean\tisRemoteModified" + GetNodeID(Field) + "()");
							JavaFile.WriteLine("{");
							JavaFile.WriteLine("return isRemoteModifiedField(\"" + GetNodeID(Field) + "\");");
							JavaFile.WriteLine("}");
						}
					}
					if (Lang == Language.JavaScript)
					{
						/*JSFile.WriteLine(CurrentJSScope + ".prototype." + "Get" + GetNodeID(Field) + "=function()"); //commented for enhancement #2380
						JSFile.WriteLine("{");
						JSFile.WriteLine("return\tthis.m_p" + GetNodeID(Field) + ";");
						JSFile.WriteLine("};");

						JSFile.WriteLine(CurrentJSScope + ".prototype." + "Set" + GetNodeID(Field) + "=function(NewVal)");
						JSFile.WriteLine("{");
						JSFile.WriteLine("this.m_p" + GetNodeID(Field) + "\t=\tNewVal;");
						JSFile.WriteLine("};");*/
						/*
						if (bRemoteModified)
						{
							JSFile.WriteLine(CurrentJSScope + ".prototype." + "IsRemoteModified" + GetNodeID(Field) + "=function()");
							JSFile.WriteLine("{");
							JSFile.WriteLine("return\tthis.IsRemoteModifiedField(\"" + GetNodeID(Field) + "\");");
							JSFile.WriteLine("};");
						}
						 */
					}

				}
				else
				{
					if (IsGroup(Field))
					{
						if (Lang == Language.CPP)
						{
							CPPFile.WriteLine(FieldType + "\t" + GenerateClassHierarchy(Prefix, Class, Lang) + "::Get" + GetNodeID(Field) + "Value()");
							CPPFile.WriteLine("{");
							CPPFile.WriteLine("return\t*m_p" + GetNodeID(Field) + "[0];");
							CPPFile.WriteLine("}");

							CPPFile.WriteLine(FieldType + "\t*" + GenerateClassHierarchy(Prefix, Class, Lang) + "::Get" + GetNodeID(Field) + "Ptr()");
							CPPFile.WriteLine("{");
							CPPFile.WriteLine("return\tm_p" + GetNodeID(Field) + "[0];");
							CPPFile.WriteLine("}");

							CPPFile.WriteLine(GenerateClassHierarchy(Prefix, Field, Lang) + "\t**" + GenerateClassHierarchy(Prefix, Class, Lang) + "::Get_" + BuildCPPGroupID(Field) + "(" + FieldType + "\t&EntriesCount)");
							CPPFile.WriteLine("{");
							CPPFile.WriteLine("if(m_p" + GetNodeID(Field) + "[0])");
							CPPFile.WriteLine("EntriesCount\t=\t*m_p" + GetNodeID(Field) + "[0];");
							CPPFile.WriteLine("else");
							CPPFile.WriteLine("EntriesCount\t=\t0;");
							CPPFile.WriteLine("return m_p" + BuildCPPGroupID(Field) + ";");
							CPPFile.WriteLine("}");


							// allocating and initializing the array element
							CPPFile.WriteLine(GenerateClassHierarchy(Prefix, Field, Lang) + "\t**" + GenerateClassHierarchy(Prefix, Class, Lang) + "::Allocate_" + BuildCPPGroupID(Field) + "(" + FieldType + "\tEntriesCount)");
							CPPFile.WriteLine("{");
							CPPFile.WriteLine("if(m_p" + BuildCPPGroupID(Field) + "\t&&\tm_p" + GetNodeID(Field) + "[0])");
							CPPFile.WriteLine("{");
							CPPFile.WriteLine("for(" + FieldType + "\tI=0;I<*m_p" + GetNodeID(Field) + "[0];I++)");
							CPPFile.WriteLine("{");
							CPPFile.WriteLine("if(m_p" + BuildCPPGroupID(Field) + "[I])");
							CPPFile.WriteLine("delete\tm_p" + BuildCPPGroupID(Field) + "[I];");
							CPPFile.WriteLine("}");

							CPPFile.WriteLine("delete\t[]\tm_p" + BuildCPPGroupID(Field) + ";");

							CPPFile.WriteLine("if(m_p" + GetNodeID(Field) + "[0])");
							CPPFile.WriteLine("if(m_p" + GetNodeID(Field) + "[1])");
							CPPFile.WriteLine("delete\tm_p" + GetNodeID(Field) + "[0];");
							CPPFile.WriteLine("else\tm_p" + GetNodeID(Field) + "[1]=m_p" + GetNodeID(Field) + "[0];");

							CPPFile.WriteLine("m_p" + GetNodeID(Field) + "[0]\t=\tNULL;");
							CPPFile.WriteLine("m_p" + BuildCPPGroupID(Field) + " = NULL;");
							CPPFile.WriteLine("}");

							CPPFile.WriteLine("if(EntriesCount\t>\t0)");

							CPPFile.WriteLine("{");
							CPPFile.WriteLine("m_p" + GetNodeID(Field) + "[0]\t=\tnew\t" + FieldType + "(EntriesCount);");
							CPPFile.WriteLine("m_p" + BuildCPPGroupID(Field) + "\t=\tnew\tC" + Prefix + BuildClassName(Field, Lang) + "\t*[EntriesCount];");
							CPPFile.WriteLine("for(int\tI=0;I<EntriesCount;I++)");
							CPPFile.WriteLine("m_p" + BuildCPPGroupID(Field) + "[I]\t=\tNULL;");
							CPPFile.WriteLine("}");
							CPPFile.WriteLine("return\tm_p" + BuildCPPGroupID(Field) + ";");
							CPPFile.WriteLine("}");
						}
						if (Lang == Language.Java)
						{
							JavaFile.WriteLine("public\tC" + Prefix + BuildClassName(Field, Lang) + "[]\tget" + BuildJavaGroupID(Field) + "()");
							JavaFile.WriteLine("{");
							JavaFile.WriteLine("return m_p" + BuildJavaGroupID(Field) + ";");
							JavaFile.WriteLine("}");
							// allocating and initializing the array element
							JavaFile.WriteLine("public\tC" + Prefix + BuildClassName(Field, Lang) + "[]\tallocate" + BuildJavaGroupID(Field) + "(" + FieldType + "\tEntriesCount)");
							JavaFile.WriteLine("{");
							JavaFile.WriteLine("if(EntriesCount\t>\t0)");
							JavaFile.WriteLine("{");
							JavaFile.WriteLine("m_p" + GetNodeID(Field) + "\t=\tEntriesCount;");
							JavaFile.WriteLine("m_p" + BuildJavaGroupID(Field) + "\t=\tnew\tC" + Prefix + BuildClassName(Field, Lang) + "\t[EntriesCount];");
							JavaFile.WriteLine("}");
							JavaFile.WriteLine("return\tm_p" + BuildJavaGroupID(Field) + ";");
							JavaFile.WriteLine("}");

							JavaFile.WriteLine("public\tvoid\tset" + BuildJavaGroupID(Field) + "(C" + Prefix + BuildClassName(Field, Lang) + "[]\tNewVal)");
							JavaFile.WriteLine("{");
							//						JavaFile.WriteLine("m_p" + GetNodeID(Field) + "\t=\tnew\t" + FieldType + "(NewVal);");
							JavaFile.WriteLine("m_p" + BuildJavaGroupID(Field) + "\t=\tNewVal;");
							JavaFile.WriteLine("}");


						}
						if (Lang == Language.JavaScript)
						{
							JSFile.WriteLine(CurrentJSScope + ".prototype." + "Get_" + BuildJSGroupID(Field) + "=function()");
							JSFile.WriteLine("{");
							JSFile.WriteLine("return this.m_p" + BuildJSGroupID(Field) + ";");
							JSFile.WriteLine("};");
							// allocating and initializing the array element
							JSFile.WriteLine(CurrentJSScope + ".prototype." + "Allocate_" + BuildJSGroupID(Field) + "=function(EntriesCount,bInitialize)");
							JSFile.WriteLine("{");
							JSFile.WriteLine("if(EntriesCount\t>\t0)");
							JSFile.WriteLine("{");
							JSFile.WriteLine("this.m_p" + GetNodeID(Field) + "\t=\tEntriesCount;");
							JSFile.WriteLine("this.m_p" + BuildJSGroupID(Field) + "=[];");
							JSFile.WriteLine("if(typeof  bInitialize === 'boolean')");
							JSFile.WriteLine("{for(var I=0;I<EntriesCount;I++){ this.m_p" + BuildJSGroupID(Field) + "[I]=new " + CurrentJSScope + "." + BuildJSClassName(Field) + "();}}");
							JSFile.WriteLine("}");
							JSFile.WriteLine("return\tthis.m_p" + BuildJSGroupID(Field) + ";");
							JSFile.WriteLine("};");
						}
					}
				}
			}
		}


		public static void DumpDestructor(string Prefix, XmlNode Class, Language Lang)
		{
			if (Lang != Language.CPP)
				return;
			HFile.WriteLine("~C" + Prefix + BuildClassName(Class, Lang) + "();");

			CPPFile.WriteLine(GenerateClassHierarchy(Prefix, Class, Lang) + "::~C" + Prefix + BuildClassName(Class, Lang) + "()");
			CPPFile.WriteLine("{");
			if (Class.Name == "message" && Class.Attributes.GetNamedItem("ExtendsMessage") == null)
			{
				CPPFile.WriteLine("if(m_pHeader)");
				CPPFile.WriteLine("delete	m_pHeader;");
				CPPFile.WriteLine("if(m_pTrailer)");
				CPPFile.WriteLine("delete	m_pTrailer;");
				CPPFile.WriteLine("m_pHeader	=	NULL;");
				CPPFile.WriteLine("m_pTrailer	=	NULL;");
			}


			foreach (XmlNode Field in Class.SelectNodes("./field|./group"))
			{
				if (FieldExistsInSuperClass(Class, Field))
					continue;
				CPPFile.WriteLine("if(m_p" + GetNodeID(Field) + "[1]!=NULL && m_p" + GetNodeID(Field) + "[1]!=m_p" + GetNodeID(Field) + "[0])");
				CPPFile.WriteLine("delete\tm_p" + GetNodeID(Field) + "[1];");
				CPPFile.WriteLine("if(m_p" + GetNodeID(Field) + "[0]!=NULL)");
				string FieldType = GetFieldType(Prefix, Field);
				if (IsGroup(Field))
				{

					CPPFile.WriteLine("{");
					CPPFile.WriteLine("for(" + FieldType + "\tI=0;I<*m_p" + GetNodeID(Field) + "[0];I++)");
					CPPFile.WriteLine("if(m_p" + BuildCPPGroupID(Field) + "[I])");
					CPPFile.WriteLine("delete\tm_p" + BuildCPPGroupID(Field) + "[I];");
					CPPFile.WriteLine("if(m_p" + BuildCPPGroupID(Field) + ")");
					CPPFile.WriteLine("delete\t[]\tm_p" + BuildCPPGroupID(Field) + ";");
					CPPFile.WriteLine("delete\tm_p" + GetNodeID(Field) + "[0];");
					CPPFile.WriteLine("}");
				}
				else
				{
					CPPFile.WriteLine("delete\tm_p" + GetNodeID(Field) + "[0];");
				}
			}
			CPPFile.WriteLine("}");
		}

		static XmlNode InsertField()
		{
			int LastChecked = 4999;
			XmlNodeList Fields = FixTop.SelectNodes("/fix/fields/field");
			XmlNode PrevField = Fields[Fields.Count - 1];

			for (int I = 0; I < Fields.Count;I++ )
			{
				XmlNode Field = Fields[I];
				int Value = int.Parse(Field.Attributes.GetNamedItem("number").Value);
				if (Value <= LastChecked)
					continue;
				if (Value > LastChecked + 1)
				{
					PrevField = Fields[I - 1];
					break;
				}
				else
				{
					LastChecked = Value;
					PrevField = Field;
				}
			}
			XmlNode NewField = PrevField.CloneNode(false);
			LastChecked++;
			NewField.Attributes.GetNamedItem("number").Value = LastChecked.ToString();
			PrevField.ParentNode.InsertAfter(NewField, PrevField);
			return NewField;
		}
		public static XmlNode FindField(string Name, string Prefix)
		{
			XmlNode FieldDef = FixTop.SelectSingleNode("/fix/fields/field[@name='" + Name + "']");
			if (FieldDef == null)
			{

				FieldDef = InsertField();
				FieldDef.Attributes.GetNamedItem("name").Value = Name;
				FieldDef.Attributes.GetNamedItem("type").Value = "AUTO";
				String FieldType = FindBaseType(FieldDef.Attributes.GetNamedItem("type").Value);
				String TypeDefName = "T" + Prefix + FieldDef.Attributes.GetNamedItem("name").Value;
				TypedefsTable[TypeDefName] = FieldType;
			}
			else
			{
				FieldDef.Attributes.RemoveNamedItem("unused");
                if (FieldDef.Attributes.GetNamedItem("type").Value != "AUTO" && DbNode.SelectSingleNode("./field[@name='" + Name + "']") != null)
					FindField(FieldDef.Attributes.GetNamedItem("type").Value, Prefix);

			}
			return FieldDef;
		}

		static string BaseClassName(string Prefix, XmlNode Node, Language Lang)
		{
			if (Node.Attributes.GetNamedItem("Extends") != null)
				return "C" + Prefix + BuildClassName(FixTop.SelectSingleNode("/fix/classes/class[@name='" + Node.Attributes.GetNamedItem("Extends").Value + "']"), Lang);

			if (Node.Attributes.GetNamedItem("ExtendsMessage") != null)
				return "C" + Prefix + BuildClassName(FixTop.SelectSingleNode("/fix/messages/message[@name='" + Node.Attributes.GetNamedItem("ExtendsMessage").Value + "']"), Lang);

			if (Node.Attributes.GetNamedItem("ExtendsRecord") != null)
				return "C" + Prefix + BuildClassName(FixTop.SelectSingleNode("/fix/records/record[@name='" + Node.Attributes.GetNamedItem("ExtendsRecord").Value + "']"), Lang);
			if (Node.Name == "message")
				return "CFIXMsg";
			if (Lang == Language.CPP && Node.Name == "record")
				return "CFIXRecord";
			return "CFIXGroup";
		}
		static XmlNode BaseClassNode(XmlNode Node)
		{
			if (Node.Attributes.GetNamedItem("Extends") != null)
				return FixTop.SelectSingleNode("/fix/classes/class[@name='" + Node.Attributes.GetNamedItem("Extends").Value + "']");

			if (Node.Attributes.GetNamedItem("ExtendsMessage") != null)
				return FixTop.SelectSingleNode("/fix/messages/message[@name='" + Node.Attributes.GetNamedItem("ExtendsMessage").Value + "']");

			if (Node.Attributes.GetNamedItem("ExtendsRecord") != null)
				return FixTop.SelectSingleNode("/fix/records/record[@name='" + Node.Attributes.GetNamedItem("ExtendsRecord").Value + "']");
			return null;
		}

		static string BaseJSClassName(string Prefix, XmlNode Node)
		{
			if (Node.Attributes.GetNamedItem("Extends") != null)
				return "C" + Prefix + "." + BuildJSClassName(FixTop.SelectSingleNode("/fix/classes/class[@name='" + Node.Attributes.GetNamedItem("Extends").Value + "']"));

			if (Node.Attributes.GetNamedItem("ExtendsMessage") != null)
				return "C" + Prefix + "." + BuildJSClassName(FixTop.SelectSingleNode("/fix/messages/message[@name='" + Node.Attributes.GetNamedItem("ExtendsMessage").Value + "']"));

			if (Node.Attributes.GetNamedItem("ExtendsRecord") != null)
				return "C" + Prefix + "." + BuildJSClassName(FixTop.SelectSingleNode("/fix/records/record[@name='" + Node.Attributes.GetNamedItem("ExtendsRecord").Value + "']"));
			if (Node.Name == "message")
				return "FIX.CFIXMsg";
			return "FIX.CFIXGroup";
		}


		public static XmlNode GetNextField(XmlNode Field)
		{
			while (Field.NextSibling != null)
			{
				XmlNode RetVal = Field.NextSibling;
				if (RetVal.Name.CompareTo("field") == 0 || RetVal.Name.CompareTo("group") == 0)
				{
					return RetVal;
				}
				Field = RetVal;
			}
			return null;
		}

		public static string HTMLEncode(XmlNode Field, string FieldType, bool bHTMLEncode)
		{
			if (FieldType.ToLower() != "string")
				return "";
			if ((Field.Attributes.GetNamedItem("HTMLEncode") != null && Field.Attributes.GetNamedItem("HTMLEncode").Value.ToLower() == "y") || bHTMLEncode)
				return "HTMLEncode";
			return "";
		}

		public static void DumpImporter(string Prefix, XmlNode Class, Language Lang,bool	bHTMLEncode)
		{
			string BinaryTags = "";
			int BinaryTagsCount = 0;
			int FieldsCount = 0;

			bool bRemoteModified = false;
			if (Class.Attributes.GetNamedItem("RemoteModified") != null && Class.Attributes.GetNamedItem("RemoteModified").Value == "Y")
			{
				bRemoteModified = true;
			}



			foreach (XmlNode Field in Class.SelectNodes("./field|./group"))
			{
				if (FieldExistsInSuperClass(Class, Field))
					continue;
				FieldsCount++;
				if (Field.Name == "field")
				{
					XmlNode FieldDef = FindField(Field.Attributes.GetNamedItem("name").Value, Prefix);
					if (FieldDef.Attributes.GetNamedItem("type").Value == "DATA")
					{
						if (BinaryTags.Length == 0)
							BinaryTags = FieldDef.Attributes.GetNamedItem("number").Value;
						else
							BinaryTags += "," + FieldDef.Attributes.GetNamedItem("number").Value;
						BinaryTagsCount++;
					}
				}
				else if (Field.Name == "group")
				{
					FindField(Field.Attributes.GetNamedItem("name").Value, Prefix);
				}
			}

			string Virtual = "";
			if (Class.Name == "class")
				Virtual = "virtual\t";
			if (Lang == Language.CPP)
			{
				HFile.WriteLine(Virtual + "bool\tImport(CMultiXBuffer\t&Buf,int\t&TagOffset,int\t&Tag,int\t&ValueOffset,int\t&ValueLength);");

				CPPFile.WriteLine("bool\t" + GenerateClassHierarchy(Prefix, Class, Lang) + "::Import(CMultiXBuffer\t&Buf,int\t&TagOffset,int\t&Tag,int\t&ValueOffset,int\t&ValueLength)");
				CPPFile.WriteLine("{");

				if (FieldsCount == 0)
					CPPFile.WriteLine("if(!" + BaseClassName(Prefix, Class, Lang) + "::Import(Buf,TagOffset,Tag,ValueOffset,ValueLength))\nreturn\tfalse;");
				else
					CPPFile.WriteLine(BaseClassName(Prefix, Class, Lang) + "::Import(Buf,TagOffset,Tag,ValueOffset,ValueLength);");

				if (FieldsCount > 0)
				{
					if (Class.Name == "group")
					{
						CPPFile.WriteLine("bool\tbFirstInGroupProcessed\t=\tfalse;");
					}
					if (BinaryTagsCount > 0)
					{
						CPPFile.WriteLine("static\tint\tBinaryTagIDs[]\t=\t{" + BinaryTags + "};");
						CPPFile.WriteLine("static\tint\tBinaryTagIDsCount\t=\t" + BinaryTagsCount + ";");
					}
					CPPFile.WriteLine("int\tSavedTagOffset\t=\tTagOffset;");
					CPPFile.WriteLine("int\tNextTagOffset\t=\tTagOffset;");
					if (BinaryTagsCount > 0)
						CPPFile.WriteLine("while(ParseField(Buf,TagOffset,Tag,ValueOffset,ValueLength,BinaryTagIDs,BinaryTagIDsCount))");
					else
						CPPFile.WriteLine("while(ParseField(Buf,TagOffset,Tag,ValueOffset,ValueLength))");
					CPPFile.WriteLine("{");
					{
						CPPFile.WriteLine("NextTagOffset\t=\tTagOffset;");
						CPPFile.WriteLine("TagOffset\t=\tSavedTagOffset;");
						CPPFile.WriteLine("switch(Tag)");
						CPPFile.WriteLine("{");
						{
							foreach (XmlNode Field in Class.SelectNodes("./field|./group"))
							{
								if (FieldExistsInSuperClass(Class, Field))
									continue;
								CPPFile.WriteLine("case\t" + Prefix + "TagID_" + Field.Attributes.GetNamedItem("name").Value + "\t:");
								{
									if (Class.Name == "group" && Field == Class.ChildNodes[0])
									{
										CPPFile.WriteLine("if(bFirstInGroupProcessed)");
										CPPFile.WriteLine("\treturn\ttrue;");
										CPPFile.WriteLine("bFirstInGroupProcessed\t=\ttrue;");
									}
									String FieldType = GetFieldType(Prefix, Field);
									CPPFile.WriteLine("if(!Import"+HTMLEncode(Field,FieldType,bHTMLEncode)+"Field(m_p" + GetNodeID(Field) + "[0],Buf,ValueOffset,ValueLength))");
										CPPFile.WriteLine("return\tfalse;");
									if (Class.Name == "trailer" && GetNextField(Field) == null)	//	this is the checksum
									{
										CPPFile.WriteLine("TagOffset\t=\tNextTagOffset;");
										CPPFile.WriteLine("return\ttrue;");
									}


									if (IsGroup(Field))
									{
										CPPFile.WriteLine("if(*m_p" + GetNodeID(Field) + "[0]\t>\t0)");
										CPPFile.WriteLine("{");
										{
											CPPFile.WriteLine("if(m_p" + BuildCPPGroupID(Field) + ")");
											{
												CPPFile.WriteLine("delete\tm_p" + BuildCPPGroupID(Field) + ";");
											}
											CPPFile.WriteLine("m_p" + BuildCPPGroupID(Field) + "\t=\tnew\tC" + Prefix + BuildClassName(Field, Lang) + "\t*[*m_p" + GetNodeID(Field) + "[0]];");
											CPPFile.WriteLine("memset(m_p" + BuildCPPGroupID(Field) + ",0,*m_p" + GetNodeID(Field) + "[0]*sizeof(*m_p" + BuildCPPGroupID(Field) + "));");
											CPPFile.WriteLine("TagOffset\t=\tNextTagOffset;");
											CPPFile.WriteLine("for(" + FieldType + "\tI=0;I<*m_p" + GetNodeID(Field) + "[0];I++)");
											CPPFile.WriteLine("{");
											{
												CPPFile.WriteLine("m_p" + BuildCPPGroupID(Field) + "[I]\t=\tnew\tC" + Prefix + BuildClassName(Field, Lang) + "();");
												CPPFile.WriteLine("if(!m_p" + BuildCPPGroupID(Field) + "[I]->Import(Buf,TagOffset,Tag,ValueOffset,ValueLength))");
												//											CPPFile.WriteLine("return\tfalse;");
												CPPFile.WriteLine("break;");
											}
											CPPFile.WriteLine("}");
										}

										CPPFile.WriteLine("NextTagOffset\t=\tTagOffset;");
										CPPFile.WriteLine("}");
									}

									CPPFile.WriteLine("break;");
								}
							}
							if (bRemoteModified)
							{
								CPPFile.WriteLine("case\t" + Prefix + "TagID_ModifiedField\t:");
								CPPFile.WriteLine("SetRemoteModifiedField(Buf,ValueOffset,ValueLength);");
								CPPFile.WriteLine("break;");
							}
							CPPFile.WriteLine("default\t:\treturn\tfalse;");
						}
						CPPFile.WriteLine("}");	//	End of case statement
						CPPFile.WriteLine("SavedTagOffset\t=\tNextTagOffset;");
						CPPFile.WriteLine("TagOffset\t=\tNextTagOffset;");
					}
					CPPFile.WriteLine("}");
				}
				CPPFile.WriteLine("return\ttrue;");
				CPPFile.WriteLine("}");
			}
			if (Lang == Language.Java)
			{
				JavaFile.WriteLine("@Override\tpublic\tboolean\tImport(CMultiXBuffer\tBuf,CFIXMsgParseParams\tParams)");
				JavaFile.WriteLine("{");
				if (FieldsCount > 0)
					JavaFile.WriteLine("super.Import(Buf,Params);");
				else
					JavaFile.WriteLine("if(!super.Import(Buf,Params))\treturn\tfalse;");

				if (FieldsCount > 0)
				{
					if (Class.Name.CompareTo("group") == 0)
					{
						JavaFile.WriteLine("boolean\tbFirstInGroupProcessed\t=\tfalse;");
					}
					if (BinaryTagsCount > 0)
					{
						JavaFile.WriteLine("Params.BinaryTagIDs\t=\tnew\tint[]{" + BinaryTags + "};");
					}
					else
					{
						JavaFile.WriteLine("Params.BinaryTagIDs\t=\tnull;");
					}

					JavaFile.WriteLine("int\tSavedTagOffset\t=\tParams.TagOffset;");
					JavaFile.WriteLine("int\tNextTagOffset\t=\tParams.TagOffset;");
					JavaFile.WriteLine("while(ParseField(Buf,Params))");
					JavaFile.WriteLine("{");
					JavaFile.WriteLine("NextTagOffset\t=\tParams.TagOffset;");
					JavaFile.WriteLine("Params.TagOffset\t=\tSavedTagOffset;");
					JavaFile.WriteLine("switch(Params.Tag)");
					JavaFile.WriteLine("{");



					bool bTestedFirstInGroup = false;
					foreach (XmlNode Field in Class.SelectNodes("./field|./group"))
					{
						if (FieldExistsInSuperClass(Class, Field))
							continue;
						JavaFile.WriteLine("case\t" + Prefix + "TagID_" + Field.Attributes.GetNamedItem("name").Value + "\t:");
						if (Class.Name.CompareTo("group") == 0 && !bTestedFirstInGroup)
						{
							JavaFile.WriteLine("if(bFirstInGroupProcessed)");
							JavaFile.WriteLine("\treturn\ttrue;");
							JavaFile.WriteLine("bFirstInGroupProcessed\t=\ttrue;");

							bTestedFirstInGroup = true;

						}
						String FieldType = GetFieldType(Prefix, Field);
                        String validFieldType = FieldType;
                        if (validFieldType.Contains("[]"))
                            validFieldType = validFieldType.Replace("[]","Array");
                        
						JavaFile.WriteLine("m_p" + GetNodeID(Field) + "=Import" + HTMLEncode(Field, FieldType, bHTMLEncode) + validFieldType + "Field(Buf,Params);");
						JavaFile.WriteLine("if(m_p" + GetNodeID(Field) + "==null)");
						JavaFile.WriteLine("return\tfalse;");



						if (Class.Name.CompareTo("trailer") == 0 && GetNextField(Field) == null) //	this is the checksum
						{
							JavaFile.WriteLine("else\t{\nParams.TagOffset\t=\tNextTagOffset;");
							JavaFile.WriteLine("return\ttrue;\n}");
						}
						else
						{
							if (IsGroup(Field))
							{
								JavaFile.WriteLine("if(m_p" + GetNodeID(Field) + "\t>\t0)");
								JavaFile.WriteLine("{");
								JavaFile.WriteLine("m_p" + BuildJavaGroupID(Field) + "\t=\tnew\tC" + Prefix + BuildClassName(Field, Lang) + "\t[m_p" + GetNodeID(Field) + "];");
								JavaFile.WriteLine("Params.TagOffset\t=\tNextTagOffset;");
								JavaFile.WriteLine("for(int\tI=0;I<m_p" + GetNodeID(Field) + ";I++)");
								JavaFile.WriteLine("{");
								JavaFile.WriteLine("m_p" + BuildJavaGroupID(Field) + "[I]\t=\tnew\tC" + Prefix + BuildClassName(Field, Lang) + "();");
								JavaFile.WriteLine("if(!m_p" + BuildJavaGroupID(Field) + "[I].Import(Buf,Params))");
								JavaFile.WriteLine("break;");
								JavaFile.WriteLine("}");
								JavaFile.WriteLine("NextTagOffset\t=\tParams.TagOffset;");
								JavaFile.WriteLine("}");

							}
							JavaFile.WriteLine("break;");
						}
					}

					if (bRemoteModified)
					{
						JavaFile.WriteLine("case\t" + Prefix + "TagID_ModifiedField\t:");
						JavaFile.WriteLine("setRemoteModifiedField(Buf,Params);");
						JavaFile.WriteLine("break;");
					}
			
					JavaFile.WriteLine("default\t:\treturn\tfalse;");
					JavaFile.WriteLine("}");	//	End of case statement
					JavaFile.WriteLine("SavedTagOffset\t=\tNextTagOffset;");
					JavaFile.WriteLine("Params.TagOffset\t=\tNextTagOffset;");
					JavaFile.WriteLine("}");
				}
				JavaFile.WriteLine("return\ttrue;");
				JavaFile.WriteLine("}");
			}
			if (Lang == Language.JavaScript)
			{
				JSFile.WriteLine(CurrentJSScope + ".prototype.Import=function(Buf,Params)");
				JSFile.WriteLine("{");
				if (FieldsCount > 0)
					JSFile.WriteLine(CurrentJSScope + ".superclass.Import.call(this,Buf,Params);");
				else
					JSFile.WriteLine("if(!" + CurrentJSScope + ".superclass.Import.call(this,Buf,Params))\treturn\tfalse;");

				if (FieldsCount > 0)
				{
					if (Class.Name.CompareTo("group") == 0)
					{
						JSFile.WriteLine("var\tbFirstInGroupProcessed\t=\tfalse;");
					}
					if (BinaryTagsCount > 0)
					{
						JSFile.WriteLine("Params.BinaryTagIDs\t=\t[" + BinaryTags + "];");
					}
					else
					{
						JSFile.WriteLine("Params.BinaryTagIDs\t=\tnull;");
					}

					JSFile.WriteLine("var\tSavedTagOffset\t=\tParams.TagOffset;");
					JSFile.WriteLine("var\tNextTagOffset\t=\tParams.TagOffset;");
					JSFile.WriteLine("while(this.ParseField(Buf,Params))");
					JSFile.WriteLine("{");
					JSFile.WriteLine("NextTagOffset\t=\tParams.TagOffset;");
					JSFile.WriteLine("Params.TagOffset\t=\tSavedTagOffset;");
					JSFile.WriteLine("switch(Params.Tag)");
					JSFile.WriteLine("{");

					bool bTestedFirstInGroup = false;
					foreach (XmlNode Field in Class.SelectNodes("./field|./group"))
					{
						if (FieldExistsInSuperClass(Class, Field))
							continue;
						JSFile.WriteLine("case\t" + TopMostJSScope + ".TagID." + Field.Attributes.GetNamedItem("name").Value + "\t:");
						if (Class.Name.CompareTo("group") == 0 && !bTestedFirstInGroup)
						{
							JSFile.WriteLine("if(bFirstInGroupProcessed)");
							JSFile.WriteLine("\t{return\ttrue;}");
							JSFile.WriteLine("bFirstInGroupProcessed\t=\ttrue;");
							bTestedFirstInGroup = true;
						}
						String FieldType = GetFieldType(Prefix, Field);

						JSFile.WriteLine("this.m_p" + GetNodeID(Field) + "=this.Import" + HTMLEncode(Field, FieldType, bHTMLEncode) + FieldType + "Field(Buf,Params);");
						JSFile.WriteLine("if(this.m_p" + GetNodeID(Field) + "===null)");
						JSFile.WriteLine("{return\tfalse;}");

						if (Class.Name.CompareTo("trailer") == 0 && GetNextField(Field) == null) //	this is the checksum
						{
							JSFile.WriteLine("else\t{\nParams.TagOffset\t=\tNextTagOffset;");
							JSFile.WriteLine("return\ttrue;\n}\nbreak;");
						}
						else
						{
							if (IsGroup(Field))
							{
								JSFile.WriteLine("if(this.m_p" + GetNodeID(Field) + "\t>\t0)");
								JSFile.WriteLine("{");
								JSFile.WriteLine("this.m_p" + BuildJSGroupID(Field) + "=[];");
								JSFile.WriteLine("Params.TagOffset\t=\tNextTagOffset;");
								JSFile.WriteLine("for(var\tI=0;I<this.m_p" + GetNodeID(Field) + ";I++)");
								JSFile.WriteLine("{");
								JSFile.WriteLine("this.m_p" + BuildJSGroupID(Field) + "[I]\t=\tnew\t" + CurrentJSScope + "." + BuildJSClassName(Field) + "();");
								JSFile.WriteLine("if(!this.m_p" + BuildJSGroupID(Field) + "[I].Import(Buf,Params))");
								JSFile.WriteLine("{break;}");
								JSFile.WriteLine("}");
								JSFile.WriteLine("NextTagOffset\t=\tParams.TagOffset;");
								JSFile.WriteLine("}");
							}
							JSFile.WriteLine("break;");
						}
					}

					JSFile.WriteLine("default\t:\treturn\tfalse;");
					JSFile.WriteLine("}");	//	End of case statement
					JSFile.WriteLine("SavedTagOffset\t=\tNextTagOffset;");
					JSFile.WriteLine("Params.TagOffset\t=\tNextTagOffset;");
					JSFile.WriteLine("}");
				}
				JSFile.WriteLine("return\ttrue;");
				JSFile.WriteLine("};");
			}
		}

		public static string GenerateClassHierarchy(string Prefix, XmlNode Class, Language Lang)
		{
            string S = "C" + Prefix + BuildClassName(Class, Lang);
            if (Lang == Language.CPP)
            {
                while (Class.Name != "message" && Class.Name != "header" && Class.Name != "trailer" && Class.Name != "record" && Class.Name != "class" && Class.Name != "list")
                {
                    Class = Class.ParentNode;
                    S = "C" + Prefix + BuildClassName(Class, Lang) + "::" + S;
                }
            }
            else if (Lang == Language.Java)
            {
                while (Class.Name != "message" && Class.Name != "header" && Class.Name != "trailer" && Class.Name != "record" && Class.Name != "class" && Class.Name != "list")
                {
                    Class = Class.ParentNode;
                    S = "C" + Prefix + BuildClassName(Class, Lang) + "." + S;
                }
            }
            return S;
		}
		public static void DumpMessageExporter(string Prefix, XmlNode Class, Language Lang)
		{
			if (Lang == Language.CPP)
			{
				HFile.WriteLine("void\tToFIX(CMultiXBuffer\t&Buf);");

				CPPFile.WriteLine("void\t" + GenerateClassHierarchy(Prefix, Class, Lang) + "::ToFIX(CMultiXBuffer\t&Buf)");
				CPPFile.WriteLine("{");
				CPPFile.WriteLine("Header().Export(Buf);");
				CPPFile.WriteLine("Export(Buf);");
				CPPFile.WriteLine("Trailer().Export(Buf);");
				CPPFile.WriteLine("AdjustLengthAndChecksum(Buf);");
				CPPFile.WriteLine("CloneRawData(Buf);");
				CPPFile.WriteLine("}");
			}
			if (Lang == Language.Java)
			{
                JavaFile.WriteLine("@Override\tpublic\tboolean\ttoFIX(CMultiXBuffer\tBuf)");
				JavaFile.WriteLine("{");
				JavaFile.WriteLine("header().Export(Buf);");
				JavaFile.WriteLine("Export(Buf);");
				JavaFile.WriteLine("trailer().Export(Buf); ");
				JavaFile.WriteLine("AdjustLengthAndChecksum(Buf);");
				JavaFile.WriteLine("CloneRawData(Buf);");
				JavaFile.WriteLine("if(checkRequiredFields())\nreturn false;\nelse return true;");
				JavaFile.WriteLine("}");

                JavaFile.WriteLine("@Override\tpublic\tboolean\ttoFIX(CMultiXBuffer\tBuf,boolean maskFields)");
                JavaFile.WriteLine("{");
                JavaFile.WriteLine("header().ExportWithMasking(Buf);");
                JavaFile.WriteLine("ExportWithMasking(Buf);");
                JavaFile.WriteLine("trailer().ExportWithMasking(Buf);");
                JavaFile.WriteLine("AdjustLengthAndChecksum(Buf);");
                JavaFile.WriteLine("CloneRawData(Buf);");
                JavaFile.WriteLine("if(checkRequiredFields())\nreturn false;\nelse return true;");
                JavaFile.WriteLine("}");
			}
			if (Lang == Language.JavaScript)
			{
				JSFile.WriteLine("this.ToFIX="+"C" + Prefix + ".ToFIX;");
			}
		}


		public static void DumpMessageImporter(string Prefix, XmlNode Class, Language Lang)
		{
			if (Lang == Language.CPP)
			{
				HFile.WriteLine("bool\tImport(CMultiXBuffer\t&Buf);");

				CPPFile.WriteLine("bool\t" + GenerateClassHierarchy(Prefix, Class, Lang) + "::Import(CMultiXBuffer\t&Buf)");
				CPPFile.WriteLine("{");
				{
					CPPFile.WriteLine("int\tTag\t=\t0;");
					CPPFile.WriteLine("int\tTagOffset\t=\t0;");
					CPPFile.WriteLine("int\tValueOffset\t=\t0;");
					CPPFile.WriteLine("int\tValueLength\t=\t0;");

					CPPFile.WriteLine("Header().Import(Buf,TagOffset,Tag,ValueOffset,ValueLength);");
					CPPFile.WriteLine("if(TagOffset\t>=\tBuf.Length())");
					CPPFile.WriteLine("return\tfalse;");


					CPPFile.WriteLine("Import(Buf,TagOffset,Tag,ValueOffset,ValueLength);");
					CPPFile.WriteLine("if(TagOffset\t>=\tBuf.Length())");
					CPPFile.WriteLine("return\tfalse;");

					CPPFile.WriteLine("if(!Trailer().Import(Buf,TagOffset,Tag,ValueOffset,ValueLength))");
					CPPFile.WriteLine("return\tfalse;");
					CPPFile.WriteLine("return\ttrue;");
				}
				CPPFile.WriteLine("}");
			}
			if (Lang == Language.Java)
			{
				JavaFile.WriteLine("@Override\tpublic\tboolean\tImport(CMultiXBuffer\tBuf)");

				JavaFile.WriteLine("{");
				JavaFile.WriteLine("CFIXMsgParseParams\tParams=new CFIXMsgParseParams();");
				JavaFile.WriteLine("header().Import(Buf,Params);");
				JavaFile.WriteLine("if(Params.TagOffset\t>=\tBuf.Length())");
				JavaFile.WriteLine("return\tfalse;");

				JavaFile.WriteLine("Import(Buf,Params);");
				JavaFile.WriteLine("if(Params.TagOffset\t>=\tBuf.Length())");
				JavaFile.WriteLine("return\tfalse;");

				JavaFile.WriteLine("if(!trailer().Import(Buf,Params))");
				JavaFile.WriteLine("return\tfalse;");
				JavaFile.WriteLine("return\ttrue;");
				JavaFile.WriteLine("}");

			}
			if (Lang == Language.JavaScript)
			{
				JSFile.WriteLine("this.ImportMsg="+"C" + Prefix + ".ImportMsg;");
			}
		}

		public static XmlNode GetPrevField(XmlNode Field)
		{
			while (Field.PreviousSibling != null)
			{
				XmlNode RetVal = Field.PreviousSibling;
				if (RetVal.Name.CompareTo("field") == 0 || RetVal.Name.CompareTo("group") == 0)
				{
					return RetVal;
				}
				Field = RetVal;
			}
			return null;
		}

		public static void DumpExporter(string Prefix, XmlNode Class, Language Lang,bool MaskSecureFields)
		{
			string Virtual = "";
			if (Class.Name == "class")
				Virtual = "virtual\t";
			
			if (Lang == Language.Java)
			{
                if (!MaskSecureFields)
                {
                    JavaFile.WriteLine("@Override\tpublic\tvoid\tExport(CMultiXBuffer\tBuf)");
                    JavaFile.WriteLine("{");
                    JavaFile.WriteLine("super.Export(Buf);");
                }
                else
                {
                    JavaFile.WriteLine("@Override\tpublic\tvoid\tExportWithMasking(CMultiXBuffer\tBuf)");
                    JavaFile.WriteLine("{");
                    JavaFile.WriteLine("super.ExportWithMasking(Buf);");
                }

                foreach (XmlNode Field in Class.SelectNodes("./field|./group"))
                {
                    if (FieldExistsInSuperClass(Class, Field))
                        continue;

                    String FieldType = GetFieldType(Prefix, Field);

                    JavaFile.WriteLine("if(m_p" + GetNodeID(Field) + "!=null)");
                    if (IsGroup(Field))
                    {
                        JavaFile.WriteLine("{");
                        JavaFile.WriteLine("int\tActualElementsCount\t=\tGetActualElementsCount(m_p" + BuildJavaGroupID(Field) + ");");
                        JavaFile.WriteLine("if(ActualElementsCount\t>\t0)");
                        JavaFile.WriteLine("{");
                        JavaFile.WriteLine("ExportField(Buf,ActualElementsCount," + Prefix + "TagID_" + Field.Attributes.GetNamedItem("name").Value + ",false);");
                        JavaFile.WriteLine("for(int\tI=0;I<m_p" + GetNodeID(Field) + ";I++)");
                        JavaFile.WriteLine("if(m_p" + BuildJavaGroupID(Field) + "[I]!=null)");
                        JavaFile.WriteLine("m_p" + BuildJavaGroupID(Field) + "[I].Export(Buf);");
                        JavaFile.WriteLine("}");
                        JavaFile.WriteLine("}");
                    }
                    else
                    {

                        XmlNode FieldDef = FindField(Field.Attributes.GetNamedItem("name").Value, Prefix);
                        XmlAttribute attr1 = (XmlAttribute)Field.Attributes.GetNamedItem("isSecure");
                        XmlAttribute attr2 = (XmlAttribute)FieldDef.Attributes.GetNamedItem("isSecure");

                        if ( MaskSecureFields && (attr1 != null && attr1.Value.Equals("Y")) || (attr2 != null && attr2.Value.Equals("Y")) )
                        {
                            if (FieldDef.Attributes.GetNamedItem("type").Value == "DATA")
                            {
                                JavaFile.WriteLine("ExportField(Buf,m_p" + GetNodeID(Field) + ",m_p" + GetNodeID(GetPrevField(Field)) + "," + Prefix + "TagID_" + Field.Attributes.GetNamedItem("name").Value + ",true);");
                            }
                            else
                            {
                                JavaFile.WriteLine("ExportField(Buf,m_p" + GetNodeID(Field) + "," + Prefix + "TagID_" + Field.Attributes.GetNamedItem("name").Value + ",true);");
                            }
                        }
                        else
                        {
                            if (FieldDef.Attributes.GetNamedItem("type").Value == "DATA")
                            {
                                JavaFile.WriteLine("ExportField(Buf,m_p" + GetNodeID(Field) + ",m_p" + GetNodeID(GetPrevField(Field)) + "," + Prefix + "TagID_" + Field.Attributes.GetNamedItem("name").Value + ",false);");
                            }
                            else
                            {
                                JavaFile.WriteLine("ExportField(Buf,m_p" + GetNodeID(Field) + "," + Prefix + "TagID_" + Field.Attributes.GetNamedItem("name").Value + ",false);");
                            }
                        }
                    }
                }
				JavaFile.WriteLine("}");
			}
			if (Lang == Language.JavaScript)
			{
				bool	bRemoteModified=false;
				if (Class.Attributes.GetNamedItem("RemoteModified") != null && Class.Attributes.GetNamedItem("RemoteModified").Value == "Y")
				{
					bRemoteModified = true;
				}

				JSFile.WriteLine(CurrentJSScope + ".prototype.Export=function(Buf)");
				JSFile.WriteLine("{");

				JSFile.WriteLine(CurrentJSScope + ".superclass.Export.call(this,Buf);");
				foreach (XmlNode Field in Class.SelectNodes("./field|./group"))
				{
					if (FieldExistsInSuperClass(Class, Field))
						continue;

					String FieldType = GetFieldType(Prefix, Field);
					JSFile.WriteLine("if(this.m_p" + GetNodeID(Field) + "!==null && this.m_p" + GetNodeID(Field) + "!==undefined)");

					if (IsGroup(Field))
					{
						JSFile.WriteLine("{");
						JSFile.WriteLine("var\tActualElementsCount\t=\tthis.GetActualElementsCount(this.m_p" + BuildJSGroupID(Field) + ");");
						JSFile.WriteLine("if(ActualElementsCount\t>\t0)");
						JSFile.WriteLine("{");
						JSFile.WriteLine("this.Export" + FieldType + "Field(Buf,ActualElementsCount," + TopMostJSScope + ".TagID." + Field.Attributes.GetNamedItem("name").Value + ");");
						JSFile.WriteLine("for(var\tI=0;I<this.m_p" + GetNodeID(Field) + ";I++)");
						JSFile.WriteLine("{if(this.m_p" + BuildJSGroupID(Field) + "[I]!==null)");
						JSFile.WriteLine("{this.m_p" + BuildJSGroupID(Field) + "[I].Export(Buf);}");
						JSFile.WriteLine("}}");
						JSFile.WriteLine("}");
					}
					else
					{
						XmlNode FieldDef = FindField(Field.Attributes.GetNamedItem("name").Value, Prefix);
						if (FieldDef.Attributes.GetNamedItem("type").Value == "DATA")
						{
							JSFile.WriteLine("{this.Export" + FieldType + "Field(Buf,this.m_p" + GetNodeID(Field) + ",this.m_p" + GetNodeID(GetPrevField(Field)) + "," + TopMostJSScope + ".TagID." + Field.Attributes.GetNamedItem("name").Value + ");}");
						}
						else
						{
							JSFile.WriteLine("{this.Export" + FieldType + "Field(Buf,this.m_p" + GetNodeID(Field) + "," + TopMostJSScope + ".TagID." + Field.Attributes.GetNamedItem("name").Value + ");}");
							if (bRemoteModified)
							{
								JSFile.WriteLine("if(this.m_p" + GetNodeID(Field) + "Modified===true)");
								JSFile.WriteLine("{this.ExportStringField(Buf,\"" + GetNodeID(Field) + "\"," + TopMostJSScope + ".TagID.ModifiedField" + ");}");
							}
						}
					}
				}
				JSFile.WriteLine("};");
			}

		}
        public static void DumpSecureFieldForMessage(Language Lang)
        {
            if (Lang != Language.Java)
                return;
            JavaFile.WriteLine("private boolean issecureMessage;");
            JavaFile.WriteLine("public boolean getIsSecure(){");
            JavaFile.WriteLine("return this.issecureMessage;");
            JavaFile.WriteLine("}");
            JavaFile.WriteLine("public void setIsSecure(boolean isSecure){");
            JavaFile.WriteLine("this.issecureMessage=isSecure;");
            JavaFile.WriteLine("}");
        }


		static bool IsGroup(XmlNode Field)
		{
			if (Field.Name != "group")
				return false;

			if (Field.HasChildNodes)
				return true;
			if (Field.Attributes.GetNamedItem("Extends") != null)
				return true;
			if (Field.Attributes.GetNamedItem("ExtendsRecord") != null)
				return true;
			return false;
		}
		public static void DumpClassCopier(string Prefix, XmlNode Class, XmlNode Other, Language Lang)
		{
			if (Lang == Language.CPP)
			{
				HFile.WriteLine("void\tCopy(" + GenerateClassHierarchy(Prefix, Other, Lang) + "\t&Other);");
				CPPFile.WriteLine("void\t" + GenerateClassHierarchy(Prefix, Class, Lang) + "::Copy(" + GenerateClassHierarchy(Prefix, Other, Lang) + "\t&Other)");
				CPPFile.WriteLine("{");
				if (Class == Other)
				{
					if (BaseClassNode(Other) != null)
					{
						CPPFile.WriteLine(GenerateClassHierarchy(Prefix, BaseClassNode(Other), Lang) + "::Copy((" + GenerateClassHierarchy(Prefix, BaseClassNode(Other), Lang) + "\t&)Other);");
					}
					foreach (XmlNode Field in Other.SelectNodes("./field|./group"))
					{
						if (FieldExistsInSuperClass(Other, Field))
							continue;
						String FieldType = GetFieldType(Prefix, Field);


						if (IsGroup(Field))
						{
							CPPFile.WriteLine("{\n" + FieldType + "\tEntriesCount=0;");
							CPPFile.WriteLine("C" + Prefix + BuildClassName(Field, Lang) + "\t**pSourceEntries;");
							CPPFile.WriteLine("C" + Prefix + BuildClassName(Field, Lang) + "\t**pDestEntries;");

							CPPFile.WriteLine("pSourceEntries=Other.Get_" + BuildCPPGroupID(Field) + "(EntriesCount);");
							CPPFile.WriteLine("pDestEntries=Allocate_" + BuildCPPGroupID(Field) + "(EntriesCount);");
							CPPFile.WriteLine("for(" + FieldType + " I=0;I<EntriesCount;I++){");
							CPPFile.WriteLine("pDestEntries[I]=new\tC" + Prefix + BuildClassName(Field, Lang) + "();");
							CPPFile.WriteLine("pDestEntries[I]->Copy(*pSourceEntries[I]);");
							CPPFile.WriteLine("}\n}");
						}
						else
						{
							CPPFile.WriteLine("if(Other.Get" + GetNodeID(Field) + "Ptr())");
							CPPFile.WriteLine("Set" + GetNodeID(Field) + "ByRef(*Other.Get" + GetNodeID(Field) + "Ptr());");
						}
					}
				}
				else
				{
					CPPFile.WriteLine(GenerateClassHierarchy(Prefix, Other, Lang) + "::Copy(Other);");
				}
				CPPFile.WriteLine("}");
			}
            else if (Lang == Language.Java)
            {
                JavaFile.WriteLine("public void copy(" + GenerateClassHierarchy(Prefix, Other, Lang) + " other)");
                JavaFile.WriteLine("{");
                if (Class == Other)
                {
                    if (BaseClassNode(Other) != null)
                    {
                        JavaFile.WriteLine("super.copy((" + GenerateClassHierarchy(Prefix, BaseClassNode(Other), Lang) + ")other);");
                    }
                    foreach (XmlNode Field in Other.SelectNodes("./field|./group"))
                    {
                        if (FieldExistsInSuperClass(Other, Field))
                            continue;
                        String FieldType = GetFieldType(Prefix, Field);


                        if (IsGroup(Field))
                        {
                            JavaFile.WriteLine("{\n" + FieldType + "\tEntriesCount=0;");
                            JavaFile.WriteLine("C" + Prefix + BuildClassName(Field, Lang) + "[]\tpSourceEntries=null;");
                            JavaFile.WriteLine("C" + Prefix + BuildClassName(Field, Lang) + "[]\tpDestEntries=null;");

                            JavaFile.WriteLine("pSourceEntries=other.get" + BuildCPPGroupID(Field) + "();");
                            JavaFile.WriteLine("pDestEntries=allocate" + BuildCPPGroupID(Field) + "(EntriesCount);");
                            JavaFile.WriteLine("for(" + FieldType + " i=0;i<EntriesCount;i++){");
                            JavaFile.WriteLine("pDestEntries[i]=new\tC" + Prefix + BuildClassName(Field, Lang) + "();");
                            JavaFile.WriteLine("pDestEntries[i].copy(pSourceEntries[i]);");
                            JavaFile.WriteLine("}\n}");
                        }
                        else
                        {
                            //CPPFile.WriteLine("if(Other.Get" + GetNodeID(Field) + "Ptr())");
                            JavaFile.WriteLine("set" + GetNodeID(Field) + "(other.get" + GetNodeID(Field) + "());");
                        }
                    }
                }
                else
                {
                    JavaFile.WriteLine("super.copy(other);");
                }
                JavaFile.WriteLine("}");
            }
		}

		public static void DumpConstructor(string Prefix, XmlNode Class, Language Lang)
		{
			XmlNode Node = Class;
			while (Node != null)
			{
				DumpClassCopier(Prefix, Class, Node, Lang);
				Node = BaseClassNode(Node);
			}
			if (Lang == Language.CPP)
			{
				HFile.WriteLine("C" + Prefix + BuildClassName(Class, Lang) + "();");
				CPPFile.WriteLine(GenerateClassHierarchy(Prefix, Class, Lang) + "::C" + Prefix + BuildClassName(Class, Lang) + "()\t:\t" + BaseClassName(Prefix, Class, Lang) + "()");
				CPPFile.WriteLine("{");
			}
			else if (Lang == Language.Java)
			{
				JavaFile.WriteLine("public\tC" + Prefix + BuildClassName(Class, Lang) + "()\n{\nsuper();");
			}
			else if (Lang == Language.JavaScript)
			{
				JSFile.WriteLine("this.Constructor=function()\n{");
				JSFile.WriteLine(CurrentJSScope + ".superclass.constructor.call(this);");
			}

			if (Class.Name == "message" && Class.Attributes.GetNamedItem("ExtendsMessage") == null)
			{
				if (Lang == Language.CPP)
				{
					CPPFile.WriteLine("m_pHeader\t=\tnew\tC" + Prefix + BuildClassName(HeaderNode, Lang) + "();");
					CPPFile.WriteLine("m_pTrailer\t=\tnew\tC" + Prefix + BuildClassName(TrailerNode, Lang) + "();");
				}
				if (Lang == Language.Java)
				{
					JavaFile.WriteLine("m_pHeader\t=\tnew\tC" + Prefix + BuildClassName(HeaderNode, Lang) + "();");
					JavaFile.WriteLine("m_pTrailer\t=\tnew\tC" + Prefix + BuildClassName(TrailerNode, Lang) + "();");
				}
				if (Lang == Language.JavaScript)
				{
					JSFile.WriteLine("this.m_pHeader\t=\tnew\t" + TopMostJSScope + "." + BuildJSClassName(HeaderNode) + "();");
					JSFile.WriteLine("this.m_pTrailer\t=\tnew\t" + TopMostJSScope + "." + BuildJSClassName(TrailerNode) + "();");
				}
			}

			foreach (XmlNode Field in Class.SelectNodes("./field|./group"))
			{
				if (FieldExistsInSuperClass(Class, Field))
					continue;
				if (Lang == Language.CPP)
					CPPFile.WriteLine("m_p" + GetNodeID(Field) + "[0]=NULL;\nm_p" + GetNodeID(Field) + "[1]=NULL;");
				if (Lang == Language.Java)
					JavaFile.WriteLine("m_p" + GetNodeID(Field) + "\t=\tnull;");

				if (Field.Attributes.GetNamedItem("default") != null)
				{
					if (Lang == Language.CPP)
						CPPFile.WriteLine("CFIXGroup::ImportField(m_p" + GetNodeID(Field) + "[0],\"" + Field.Attributes.GetNamedItem("default").Value + "\");");
					if (Lang == Language.Java)
					{
						JavaFile.WriteLine("m_p" + GetNodeID(Field) + "=Import" + GetFieldType(Prefix, Field) + "Field(\"" + Field.Attributes.GetNamedItem("default").Value + "\");");
					}
					if (Lang == Language.JavaScript)
					{
						JSFile.WriteLine("this.m_p" + GetNodeID(Field) + "=this.Import" + GetFieldType(Prefix, Field) + "FieldFromString(\"" + Field.Attributes.GetNamedItem("default").Value + "\");");
					}

				}

				if (IsGroup(Field))
				{
					if (Lang == Language.CPP)
						CPPFile.WriteLine("m_p" + BuildCPPGroupID(Field) + "=NULL;");
					if (Lang == Language.Java)
						JavaFile.WriteLine("m_p" + BuildJavaGroupID(Field) + "=null;");

				}
			}
			//			DumpRequiredFields(Class,Lang);
			if (Class.Name == "message" && Class.Attributes.GetNamedItem("ExtendsMessage") != null)
			{
				if (Lang == Language.CPP)
				{
					CPPFile.WriteLine("Header().SetBeginStringValue(\"" + DefaultBeginString + "\");");
					CPPFile.WriteLine("Header().SetBodyLengthValue(99999);");
				}

				if (Lang == Language.Java)
				{
					JavaFile.WriteLine("header().setBeginString(\"" + DefaultBeginString + "\");");
					JavaFile.WriteLine("header().setBodyLength(999999);");
				}
				if (Lang == Language.JavaScript)
				{
					/*JSFile.WriteLine("this.m_pHeader.SetBeginString(\"" + DefaultBeginString + "\");");
					JSFile.WriteLine("this.m_pHeader.SetBodyLength(999999);");*/
					
					JSFile.WriteLine("this.m_pHeader.m_pBeginString=\"" + DefaultBeginString + "\";");
					JSFile.WriteLine("this.m_pHeader.m_pBodyLength=999999;");
				}


				if (Class.Attributes.GetNamedItem("msgtype") != null)
				{
					if (Lang == Language.CPP)
					{
						if (bMsgTypeIsNumeric)
							CPPFile.WriteLine("Header().SetMsgTypeValue(" + Class.Attributes.GetNamedItem("msgtype").Value + ");");
						else
							CPPFile.WriteLine("Header().SetMsgTypeValue(\"" + Class.Attributes.GetNamedItem("msgtype").Value + "\");");
					}
					if (Lang == Language.Java)
					{
						if (bMsgTypeIsNumeric)
						{
							JavaFile.WriteLine("header().setMsgType(" + Class.Attributes.GetNamedItem("msgtype").Value + ");");
						}
						else
						{
							JavaFile.WriteLine("header().setMsgType(\"" + Class.Attributes.GetNamedItem("msgtype").Value + "\");");
						}
					}

					if (Lang == Language.JavaScript)
					{
						/*if (bMsgTypeIsNumeric)
						{
							JSFile.WriteLine("this.m_pHeader.SetMsgType(" + Class.Attributes.GetNamedItem("msgtype").Value + ");");
						}
						else
						{
							JSFile.WriteLine("this.m_pHeader.SetMsgType(\"" + Class.Attributes.GetNamedItem("msgtype").Value + "\");");
						}*/
						if (bMsgTypeIsNumeric)
						{
							JSFile.WriteLine("this.m_pHeader.m_pMsgType=" + Class.Attributes.GetNamedItem("msgtype").Value + ";");
						}
						else
						{
							JSFile.WriteLine("this.m_pHeader.m_pMsgType=\"" + Class.Attributes.GetNamedItem("msgtype").Value + "\";");
						}
					}
				}

				if (Lang == Language.CPP)
					CPPFile.WriteLine("Trailer().SetCheckSumValue(255);");
				if (Lang == Language.Java)
					JavaFile.WriteLine("trailer().setCheckSum(255);");
				if (Lang == Language.JavaScript)
					JSFile.WriteLine("this.m_pTrailer.m_pCheckSum=255;"); //JSFile.WriteLine("this.m_pTrailer.SetCheckSum(255);");

			}
			if (Lang == Language.CPP)
				CPPFile.WriteLine("}");
			if (Lang == Language.Java)
				JavaFile.WriteLine("}");
			if (Lang == Language.JavaScript)
				JSFile.WriteLine("};");
		}

		public static string BuildClassName(XmlNode Class, Language Lang)
		{
			string ClassName = "";
			try
			{
				if (Lang == Language.Java)
				{
					if (Class.Name == "group")
					{
						if (Class.Attributes.GetNamedItem("groupname") != null)
							ClassName = Class.Name.Substring(0, 1).ToUpper() + Class.Attributes.GetNamedItem("groupname").Value;
						else
							ClassName = Class.Name.Substring(0, 1).ToUpper() + Class.Attributes.GetNamedItem("name").Value;
					}
					else if (Class.Attributes.GetNamedItem("name") != null)
						ClassName = Class.Name.Substring(0, 1).ToUpper() + Class.Attributes.GetNamedItem("name").Value;
					else
						ClassName = Class.Name.ToUpper();
				}
				else
				{
					if (Class.Name == "group")
					{
						if (Class.Attributes.GetNamedItem("groupname") != null)
							ClassName = Class.Name + "_" + Class.Attributes.GetNamedItem("groupname").Value;
						else
							ClassName = Class.Name + "_" + Class.Attributes.GetNamedItem("name").Value;
					}
					else if (Class.Attributes.GetNamedItem("name") != null)
						ClassName = Class.Name + "_" + Class.Attributes.GetNamedItem("name").Value;
					else
						ClassName = Class.Name;
				}
			}
			catch
			{
				ClassName = Class.Name;
			}

			return ClassName;
		}


		public static String BuildJSClassName(XmlNode Class)
		{
			String ClassName = "";
			try
			{
				if (Class.Name == "group")
				{
					if (Class.Attributes.GetNamedItem("groupname") != null)
						ClassName = Class.Name + Class.Attributes.GetNamedItem("groupname").Value;
					else
						ClassName = Class.Name + Class.Attributes.GetNamedItem("name").Value;
				}
				else
				{
					ClassName = Class.Name + '.' + Class.Attributes.GetNamedItem("name").Value;
				}
			}
			catch
			{
				ClassName = Class.Name;
			}
			return ClassName;
		}

		public static String BuildJSShortClassName(XmlNode Class)
		{
			String ClassName = "";
			try
			{
				if (Class.Name.CompareTo("group") == 0)
				{
					if (Class.Attributes.GetNamedItem("groupname") != null)
						ClassName = Class.Attributes.GetNamedItem("groupname").Value;
					else
						ClassName = Class.Attributes.GetNamedItem("name").Value;
				}
				else
				{
					ClassName = Class.Name + '.' + Class.Attributes.GetNamedItem("name").Value;
				}
			}
			catch
			{
				ClassName = Class.Name;
			}
			return ClassName;
		}

		public static String BuildJSGroupID(XmlNode Class)
		{
			String ClassName = "";
			try
			{
				if (Class.Name.CompareTo("group") == 0)
				{
					{
						if (Class.Attributes.GetNamedItem("groupid") != null)
							ClassName = Class.Attributes.GetNamedItem("groupid").Value;
						else
							ClassName = Class.Name + GetNodeID(Class);
					}
				}
			}
			catch
			{
				ClassName = Class.Name;
			}
			return ClassName;
		}

		public static String BuildJavaGroupID(XmlNode Class)
		{
			String ClassName = "";
			try
			{
				if (Class.Name.CompareTo("group") == 0)
				{
					if (Class.Attributes.GetNamedItem("groupid") != null)
						ClassName = Class.Attributes.GetNamedItem("groupid").Value;
					else
						ClassName = "G" + GetNodeID(Class);
				}
			}
			catch
			{
				ClassName = Class.Name;
			}
			return ClassName;
		}
		public static String BuildCPPGroupID(XmlNode Class)
		{
			String ClassName = "";
			try
			{
				if (Class.Name.CompareTo("group") == 0)
				{
					if (Class.Attributes.GetNamedItem("groupid") != null)
						ClassName = Class.Attributes.GetNamedItem("groupid").Value;
					else
						ClassName = Class.Name + '_' + GetNodeID(Class);
				}
			}
			catch
			{
				ClassName = Class.Name;
			}
			return ClassName;
		}
		public static string GetSQLType(XmlNode Field, bool bWithSize, bool isEncrypted)
		{

			XmlNode FieldDef = FixTop.SelectSingleNode("/fix/fields/field[@name='" + Field.Attributes.GetNamedItem("name").Value + "']");

			try
			{
                while (DbNode.SelectSingleNode("./field[@name='" + FieldDef.Attributes.GetNamedItem("type").Value + "']") == null)
				{
					FieldDef = FixTop.SelectSingleNode("/fix/fields/field[@name='" + FieldDef.Attributes.GetNamedItem("type").Value + "']");
				}
			}
			catch
			{
				if (bWithSize)
					return "varchar(255)";
				return "varchar";
			}
            XmlNode PrimFieldNode = DbNode.SelectSingleNode("./field[@name='" + FieldDef.Attributes.GetNamedItem("type").Value + "']");
			string SQLType = "varchar";
            string Size = null;
			try
			{
				SQLType = PrimFieldNode.Attributes.GetNamedItem("SQLType").Value;
                if (isEncrypted)
                {
                    if(PrimFieldNode.Attributes.GetNamedItem("EncryptSQLType")==null || PrimFieldNode.Attributes.GetNamedItem("EncryptSQLType").Value==null
                        || PrimFieldNode.Attributes.GetNamedItem("EncryptSQLType").Value.Length<=0)
                    {
                        Console.Error.WriteLine("ERROR::Encryption Type not defined for SQLType: " + SQLType);
                        Environment.Exit(1);
                    }
                    SQLType = PrimFieldNode.Attributes.GetNamedItem("EncryptSQLType").Value;
                }
			}
			catch
			{
			}
			if (bWithSize)
			{
                if (isEncrypted && Field.Attributes.GetNamedItem("EncryptSQLSize") != null && Field.Attributes.GetNamedItem("EncryptSQLSize").Value.Length > 0)
                {
                    Size = Field.Attributes.GetNamedItem("EncryptSQLSize").Value;
                }             
                else if (Field.Attributes.GetNamedItem("SQLSize") != null && Field.Attributes.GetNamedItem("SQLSize").Value.Length > 0)
				{
					Size = Field.Attributes.GetNamedItem("SQLSize").Value;
				}
                else if (isEncrypted && PrimFieldNode.Attributes.GetNamedItem("EncryptSQLSize") != null && PrimFieldNode.Attributes.GetNamedItem("EncryptSQLSize").Value.Length > 0)
                {
                    Size = PrimFieldNode.Attributes.GetNamedItem("EncryptSQLSize").Value;
                }
				else if (PrimFieldNode.Attributes.GetNamedItem("SQLSize") != null && PrimFieldNode.Attributes.GetNamedItem("SQLSize").Value.Length > 0)
				{
					Size = PrimFieldNode.Attributes.GetNamedItem("SQLSize").Value;
				}				
                else
                    if (SQLType.ToLower() == "varchar")
                    {
                        Size = "255";
                    }
                    else if (SQLType.ToLower() == "decimal")
                    {
                        Size = "25,4";
                    }
                if (Size != null)
                {
                    SQLType += "(" + Size + ")";
                }
                
           }
            
			return SQLType;
		}

		public static int GetSQLTypeSize(XmlNode Field)
		{
			int SQLTypeSize = 0;
			XmlNode FieldDef = FixTop.SelectSingleNode("/fix/fields/field[@name='" + Field.Attributes.GetNamedItem("name").Value + "']");

            while (DbNode.SelectSingleNode("./field[@name='" + FieldDef.Attributes.GetNamedItem("type").Value + "']") == null)
			{
				FieldDef = FixTop.SelectSingleNode("/fix/fields/field[@name='" + FieldDef.Attributes.GetNamedItem("type").Value + "']");
			}


			string SQLType = "varchar";
			try
			{
                SQLType = DbNode.SelectSingleNode("./field[@name='" + FieldDef.Attributes.GetNamedItem("type").Value + "']").Attributes.GetNamedItem("SQLType").Value;
			}
			catch
			{
			}
			if (Field.Attributes.GetNamedItem("SQLSize") != null && Field.Attributes.GetNamedItem("SQLSize").Value.Length > 0)
			{
				SQLTypeSize = int.Parse(Field.Attributes.GetNamedItem("SQLSize").Value);
			}
			else
				if (SQLType.ToLower() == "varchar")
				{
					SQLTypeSize = 255;
				}
			return SQLTypeSize;
		}

		static private String SQLRecords = "";
		static bool FieldExistsInSuperClass(XmlNode Class, XmlNode CurrentField)
		{

			XmlNode Super = BaseClassNode(Class);
			while (Super != null)
			{
				if (CurrentField.Name == "field" && Super.SelectSingleNode("./field[@name='" + CurrentField.Attributes.GetNamedItem("name").Value + "']") != null)
					return true;
				if (CurrentField.Name == "group" && Super.SelectSingleNode("./group[@name='" + CurrentField.Attributes.GetNamedItem("name").Value + "']") != null)
					return true;
				if (CurrentField.Name == "component" && Super.SelectSingleNode("./component[@name='" + CurrentField.Attributes.GetNamedItem("name").Value + "']") != null)
					return true;
				Super = BaseClassNode(Super);
			}
			return false;
		}
		static public string SQLQueryOperator(XmlNode Field)
		{
			if (Field.Attributes.GetNamedItem("SQLQueryOperator") != null)
				return Field.Attributes.GetNamedItem("SQLQueryOperator").Value.ToLower();
			return "=";
		}
		static public string SQLSortOperator(XmlNode Field)
		{
			if (Field.Attributes.GetNamedItem("SQLDir") != null)
				return Field.Attributes.GetNamedItem("SQLDir").Value;
			return "";
		}


		static public XmlNode BuildCombinedClass(XmlNode Class)
		{
			XmlNode RetVal = Class.Clone();
			XmlNode First = RetVal.FirstChild;
			XmlNode Node = BaseClassNode(Class);
			while (Node != null)
			{
				foreach (XmlNode Field in Node.SelectNodes("./field|./index|./query"))
				{
					if (Field.Name == "field" && RetVal.SelectSingleNode("./field[@name='" + Field.Attributes.GetNamedItem("name").Value + "']") != null)
						continue;
					if (Field.Name == "index" && RetVal.SelectSingleNode("./index[@name='" + Field.Attributes.GetNamedItem("name").Value + "']") != null)
						continue;
					if (Field.Name == "query" && RetVal.SelectSingleNode("./query[@name='" + Field.Attributes.GetNamedItem("name").Value + "']") != null)
						continue;
					RetVal.InsertBefore(Field.Clone(), First);
				}
				Node = BaseClassNode(Node);
			}
			return RetVal;
		}

		public static void DumpSQLSelectCommand(string Prefix, XmlNode Class, Language Lang, XmlNode View, string FuncName, string FuncParams, string WhereClause, string OrderByClause)
		{
			HFile.WriteLine("void\t" + FuncName + FuncParams + ",bool bForUpdate,int MaxCount=0);");
			CPPFile.WriteLine("void\t" + GenerateClassHierarchy(Prefix, Class, Lang) + "::" + FuncName + FuncParams + ",bool bForUpdate,int MaxCount)");
			CPPFile.WriteLine("{");
			CPPFile.Write("Cmd = \"select ");
			bool	bFirst = true;
			foreach (XmlNode Field in View.SelectNodes("./field"))
			{
				XmlNode ClassField = Class.SelectSingleNode("./field[@name='" + Field.Attributes.GetNamedItem("name").Value + "']");
				if (bFirst)
				{
					CPPFile.Write(GetNodeID(ClassField));
					bFirst = false;
				}
				else
				{
					CPPFile.Write("," + GetNodeID(ClassField));
				}
			}
			CPPFile.WriteLine("\"\n\" from \" + SQLTableName() + ");
			CPPFile.WriteLine("\" where \";");
			CPPFile.WriteLine(WhereClause);
			if (OrderByClause.Length > 0)
			{
				CPPFile.WriteLine("Cmd+=\" order by " + OrderByClause.Substring(0, OrderByClause.Length - 1) + "\";");
			}
			CPPFile.WriteLine("if(MaxCount>0)\n{\nchar B[100];\nsprintf(B,\" limit %d \",MaxCount);\nCmd += B;\n}");
			CPPFile.WriteLine("if(bForUpdate)\nCmd += \" for update \";");
			CPPFile.WriteLine("}");

		}

		public static void DumpSQLSupport(string Prefix, XmlNode Class, Language Lang)
		{
			if (Class.Name != "record")
				return;
			if (Lang == Language.Java && Class.Attributes.GetNamedItem("SQLTable") != null)
			{
				Class = BuildCombinedClass(Class);
                string recordName = Class.Attributes.GetNamedItem("name").Value;
				StreamWriter XmlF = new StreamWriter(JavaDir + "/" + BuildClassName(Class, Lang) + ".hbm.xml");
                XmlNode EncRecord = EncryptTop.SelectSingleNode("./records/record[@name='" + recordName + "']");
				XmlF.WriteLine("<?xml version=\"1.0\"?>");
				XmlF.WriteLine("<!DOCTYPE hibernate-mapping PUBLIC \"-//Hibernate/Hibernate Mapping DTD 3.0//EN\" \"http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd\">");
				XmlF.WriteLine("<!-- Generated " + DateTime.Now.ToString() + " by mFino FIXMLCodeGenerator -->");
				XmlF.WriteLine("<hibernate-mapping>");
                if (EncRecord != null)
                {
                    XmlF.WriteLine("<typedef name=\"encryptedBigDecimal\" class=\"org.jasypt.hibernate.type.EncryptedBigDecimalAsStringType\">");
                    XmlF.WriteLine("<param name=\"encryptorRegisteredName\">hibernateStringEncryptor</param>");
                    XmlF.WriteLine("</typedef>");
                    XmlF.WriteLine("<typedef name=\"encryptedString\" class=\"org.jasypt.hibernate.type.EncryptedStringType\">");
                    XmlF.WriteLine("<param name=\"encryptorRegisteredName\">hibernateStringEncryptor</param>");
                    XmlF.WriteLine("</typedef>");
                    XmlF.WriteLine("<typedef name=\"uniqueencryptedString\" class=\"org.jasypt.hibernate.type.EncryptedStringType\">");
                    XmlF.WriteLine("<param name=\"encryptorRegisteredName\">hibernateUniqueStringEncryptor</param>");
                    XmlF.WriteLine("</typedef>");
                }
                XmlNode VersionCol = Class.SelectSingleNode("./field[@HibernateVersion='Y']");
                string tableName = Class.Attributes.GetNamedItem("SQLTable").Value;
				if (VersionCol == null)
					//XmlF.WriteLine("<class name=\"com.mfino.domain." + Class.Attributes.GetNamedItem("name").Value + "\" table=\"" + Class.Attributes.GetNamedItem("SQLTable").Value + "\" catalog=\"mfino\">");
                    XmlF.WriteLine("<class name=\"com.mfino.domain." + Class.Attributes.GetNamedItem("name").Value + "\" table=\"" + tableName + "\" >");
				else
					//XmlF.WriteLine("<class name=\"com.mfino.domain." + Class.Attributes.GetNamedItem("name").Value + "\" optimistic-lock=\"version\" table=\"" + Class.Attributes.GetNamedItem("SQLTable").Value + "\" catalog=\"mfino\">");
                    XmlF.WriteLine("<class name=\"com.mfino.domain." + Class.Attributes.GetNamedItem("name").Value + "\" optimistic-lock=\"version\" table=\"" + tableName + "\" >");
            

				foreach (XmlNode Field in Class.SelectNodes("./field"))
				{
					if (GetNodeID(Field) == "ID")
					{
						XmlF.WriteLine("<id name=\"ID\" type=\"java.lang.Long\">");
						XmlF.WriteLine("<column name=\"ID\" />");
                        if (Field.Attributes.GetNamedItem("AutoIncrement") != null && Field.Attributes.GetNamedItem("AutoIncrement").Value == "Y")
                        {
                            XmlF.WriteLine("<generator class=\"native\">");
                            XmlF.WriteLine("<param name=\"sequence\">"+tableName+"_ID_SEQ</param>");    
                            XmlF.WriteLine("</generator>");
                        }
                        else if (Field.Attributes.GetNamedItem("OneToOne") != null)
                            XmlF.WriteLine("<generator class=\"foreign\" >" + "<param name=\"property\">" + Field.Attributes.GetNamedItem("OneToOne").Value + "</param></generator>");
						XmlF.WriteLine("</id>");
						break;
					}
				}

				if (VersionCol != null)
				{
					XmlF.WriteLine("<version name=\"" + GetNodeID(VersionCol) + "\" type=\"" + GetHibernateType(VersionCol, false, false) + "\">");
					XmlF.WriteLine("<column name=\"" + GetNodeID(VersionCol) + "\" " + (VersionCol.Attributes.GetNamedItem("required").Value == "Y" ? "not-null=\"true\"" : " ") + " " + GetHibernateLength(VersionCol) + " />");
					XmlF.WriteLine("</version>");
				}

				foreach (XmlNode Field in Class.SelectNodes("./field"))
				{
					string indexString = null;
					if (GetNodeID(Field) != "ID")
					{
						indexString = null;
						foreach (XmlNode index in Class.SelectNodes("./index"))
						{
							//skip primary key
							if (index.Attributes.GetNamedItem("PrimaryKey") != null
									&& index.Attributes.GetNamedItem("PrimaryKey").Value == "Y")
							{
								continue;
							}
							//return the index the column participates in
							foreach (XmlNode indexField in index.SelectNodes("./field"))
							{
								if (indexField.Attributes["name"].Value == Field.Attributes["name"].Value)
								{
									string indexName = index.Attributes["name"].Value;
									string indexType = (index.Attributes["Unique"] != null
											&& index.Attributes["Unique"].Value == "Y") ? "unique-key" : "index";

									indexString = string.Format("{0}=\"{1}\"", indexType, indexName);
									break;
								}
							}
						}
					}
					if (Field.Attributes.GetNamedItem("References") != null && FixTop.SelectSingleNode("./records/record[@name='" + Field.Attributes.GetNamedItem("References").Value + "']") != null)
					{
						string referenceRecordName = Field.Attributes.GetNamedItem("References").Value;
						string referenceName = GetNodeID(Field);
						referenceName = referenceName.Substring(0, referenceName.Length - 2);
						if (!referenceName.Equals(referenceRecordName))
						{
							referenceName = referenceRecordName + "By" + GetNodeID(Field);
						}

						XmlNode Record = FixTop.SelectSingleNode("./records/record[@name='" + Field.Attributes.GetNamedItem("References").Value + "']");
						string className = BuildClassName(Record, Lang).Substring(1);

						JavaFile.WriteLine("public\tstatic\tfinal\tString\t" + "FieldName_" + referenceName + "\t=\t\"" + referenceName + "\";");

						JavaFile.WriteLine("private " + className + " m_p" + referenceName + ";");
						JavaFile.WriteLine("public " + className + " get" + referenceName + "(){");
						JavaFile.WriteLine("return m_p" + referenceName + ";\n}");
						JavaFile.WriteLine("public void set" + referenceName + "(" + className + " NewVal){");
						JavaFile.WriteLine("m_p" + referenceName + "=NewVal;\n}");

						XmlF.WriteLine("<many-to-one name=\"" + referenceName + "\" foreign-key=\"FK_" + Class.Attributes["name"].Value + "_" + referenceName + "\" class=\"com.mfino.domain." + Field.Attributes.GetNamedItem("References").Value + "\" fetch=\"select\">");
						XmlF.WriteLine("<column name=\"" + GetNodeID(Field) + "\" " + (Field.Attributes.GetNamedItem("required").Value == "Y" ? "not-null=\"true\"" : " ") + " " + GetHibernateLength(Field) + " " + indexString + " />");
						XmlF.WriteLine("</many-to-one>");
					}
					else if (Field.Attributes.GetNamedItem("OneToOne") != null && FixTop.SelectSingleNode("./records/record[@name='" + Field.Attributes.GetNamedItem("OneToOne").Value + "']") != null)
					{
						string referenceRecordName = Field.Attributes.GetNamedItem("OneToOne").Value;
						/*
						string referenceName = GetNodeID(Field);
						referenceName = referenceName.Substring(0, referenceName.Length - 2);
						if (!referenceName.Equals(referenceRecordName))
						{
							referenceName = referenceRecordName + "By" + GetNodeID(Field);
						}
						*/
						XmlNode Record = FixTop.SelectSingleNode("./records/record[@name='" + Field.Attributes.GetNamedItem("OneToOne").Value + "']");
						string className = BuildClassName(Record, Lang).Substring(1);

						JavaFile.WriteLine("public\tstatic\tfinal\tString\t" + "FieldName_" + referenceRecordName + "\t=\t\"" + referenceRecordName + "\";");

						JavaFile.WriteLine("private " + className + " m_p" + referenceRecordName + ";");
						JavaFile.WriteLine("public " + className + " get" + referenceRecordName + "(){");
						JavaFile.WriteLine("return m_p" + referenceRecordName + ";\n}");
						JavaFile.WriteLine("public void set" + referenceRecordName + "(" + className + " NewVal){");
						JavaFile.WriteLine("m_p" + referenceRecordName + "=NewVal;\n}");

						XmlF.WriteLine("<one-to-one name=\"" + referenceRecordName + "\"  constrained=\"true\"/>");
					}
					else if (!(Field.Attributes.GetNamedItem("HibernateVersion") != null && Field.Attributes.GetNamedItem("HibernateVersion").Value == "Y"))
					{
						if (GetNodeID(Field) != "ID")
						{
							string defaultValue = string.Empty;
							if (Field.Attributes["SQLDefault"] != null)
							{
								defaultValue = " default=\"'" + Field.Attributes["SQLDefault"].Value + "'\" ";
							}
                            bool isEncrypted = false;
                            bool isUnique = false;
                           
                            if (EncRecord != null)
                            {
                                XmlNode EncField = EncRecord.SelectSingleNode("./field[@name='" + GetNodeID(Field) + "']");
                                if (EncField != null)
                                {
                                    isEncrypted = true;
                                    XmlAttribute attr = (XmlAttribute)EncField.Attributes.GetNamedItem("unique");
                                    if (attr != null && attr.Value == "Yes")
                                    {
                                        isUnique = true;
                                    }
                                }
                            }
							XmlF.WriteLine("<property name=\"" + GetNodeID(Field) + "\" type=\"" + GetHibernateType(Field, isEncrypted, isUnique) + "\" " + indexString + ">");
							XmlF.WriteLine("<column name=\"" + GetNodeID(Field) + "\" " 
                                + (Field.Attributes.GetNamedItem("required").Value == "Y" ? "not-null=\"true\"" : " ") 
                                + " " + GetHibernateLength(Field) + defaultValue 
                                + " sql-type=\"" + GetSQLType(Field, true, isEncrypted) + "\" />");
							XmlF.WriteLine("</property>");
						}
					}
				}

				foreach (XmlNode Other in FixTop.SelectNodes("./records/record"))
				{
					XmlNode N = BuildCombinedClass(Other);
					foreach (XmlNode F in N.SelectNodes("./field[@References='" + Class.Attributes.GetNamedItem("name").Value + "']"))
					{
						string referenceName = N.Attributes.GetNamedItem("name").Value;
						string setName = GetNodeID(F);
						setName = referenceName + "From" + setName;
						string className = BuildClassName(N, Lang).Substring(1);

						JavaFile.WriteLine("public\tstatic\tfinal\tString\t" + "FieldName_" + setName + "\t=\t\"" + setName + "\";");

						JavaFile.WriteLine("private Set<" + className + "> m_p" + setName + " = new HashSet<" + className + ">(0);");
						JavaFile.WriteLine("public Set<" + className + "> get" + setName + "() {");
						JavaFile.WriteLine("return m_p" + setName + ";\n}");
						JavaFile.WriteLine("public void set" + setName + "(Set<" + className + "> NewValue) {");
						JavaFile.WriteLine("m_p" + setName + " = NewValue;\n}");

						XmlF.WriteLine("<set name=\"" + setName + "\" inverse=\"true\">");
						XmlF.WriteLine("<key>");
						XmlF.WriteLine("<column name=\"" + GetNodeID(F) + "\" " + (F.Attributes.GetNamedItem("required").Value == "Y" ? "not-null=\"true\"" : " ") + " " + GetHibernateLength(F) + " />");
						XmlF.WriteLine("</key>");
						XmlF.WriteLine("<one-to-many class=\"com.mfino.domain." + referenceName + "\" />");
						XmlF.WriteLine("</set>");
					}
					foreach (XmlNode F in N.SelectNodes("./field[@OneToOne='" + Class.Attributes.GetNamedItem("name").Value + "']"))
					{
						string referenceRecordName = N.Attributes.GetNamedItem("name").Value;
//						string referenceName = referenceRecordName + "ByID";

						string className = BuildClassName(N, Lang).Substring(1);

						JavaFile.WriteLine("public\tstatic\tfinal\tString\t" + "FieldName_" + referenceRecordName + "\t=\t\"" + referenceRecordName + "\";");

						JavaFile.WriteLine("private " + className + " m_p" + referenceRecordName + ";");
						JavaFile.WriteLine("public " + className + " get" + referenceRecordName + "(){");
						JavaFile.WriteLine("return m_p" + referenceRecordName + ";\n}");
						JavaFile.WriteLine("public void set" + referenceRecordName + "(" + className + " NewVal){");
						JavaFile.WriteLine("m_p" + referenceRecordName + "=NewVal;\n}");

						XmlF.WriteLine("<one-to-one name=\"" + referenceRecordName + "\" />");
					}
				}

				XmlF.WriteLine("</class>\n</hibernate-mapping>");
				XmlF.Close();
			}
		}


		public static void DumpJSFieldsName(XmlNode Class)
		{
			XmlNode BaseClass = Class;
			do
			{

				foreach (XmlNode Field in BaseClass.SelectNodes("./field|./group"))
				{
					AllJSFieldsNames += CurrentJSShortScope + "." + GetNodeID(Field) + "={_name:'" + GetNodeID(Field) + "'};\n";
				}

				BaseClass = BaseClassNode(BaseClass);

			} while (BaseClass != null);
		}
		public static void DumpClass(string Prefix, XmlNode Class, string MemberName, Language Lang,bool	bHTMLEncode)
		{
			if (Class.Attributes.GetNamedItem("HTMLEncode") != null && Class.Attributes.GetNamedItem("HTMLEncode").Value.ToLower() == "n")
				bHTMLEncode = false;
			else	if (Class.Attributes.GetNamedItem("HTMLEncode") != null && Class.Attributes.GetNamedItem("HTMLEncode").Value.ToLower() == "y")
				bHTMLEncode = true;
			if (Lang == Language.CPP)
				HFile.WriteLine("class\tEXPORTABLE_FIX_API\tC" + Prefix + BuildClassName(Class, Lang) + "\t:\tpublic\t" + BaseClassName(Prefix, Class, Lang));
			if (Lang == Language.Java)
				JavaFile.WriteLine("public\tstatic\tclass\tC" + Prefix + BuildClassName(Class, Lang) + "\textends\t" + BaseClassName(Prefix, Class, Lang));

			if (Lang == Language.CPP)
			{
				HFile.WriteLine("{");
				HFile.WriteLine("public:");
			}
			if (Lang == Language.Java)
				JavaFile.WriteLine("{");

			if (MemberName != null)
			{
				if (Lang == Language.JavaScript)
				{
					AllJSFieldsNames += CurrentJSShortScope + "." + BuildJSShortClassName(Class) + "={};\n";
					AllJSFieldsNames += CurrentJSShortScope + "." + BuildJSShortClassName(Class) + "._name='" + MemberName + "';\n";
				}
			}
			String SavedJSScope = CurrentJSScope;
			String SavedJSShortScope = CurrentJSShortScope;
			String JsonFields = "";
			bool bIsFirst = true;

			if (Lang == Language.JavaScript)
			{
				JSFile.WriteLine(CurrentJSScope + "." + BuildJSClassName(Class) + "=function()");
				JSFile.WriteLine("{");

				CurrentJSScope += "." + BuildJSClassName(Class);
				CurrentJSShortScope += "." + BuildJSShortClassName(Class);
			}


			if (Class.Name == "message" && Class.Attributes.GetNamedItem("ExtendsMessage") == null)
			{
				if (Lang == Language.CPP)
				{
					HFile.WriteLine("C" + Prefix + BuildClassName(HeaderNode, Lang) + "\t*m_pHeader;");
					HFile.WriteLine("C" + Prefix + BuildClassName(TrailerNode, Lang) + "\t*m_pTrailer;");
				}
				if (Lang == Language.Java)
				{
					JavaFile.WriteLine("public\tC" + Prefix + BuildClassName(HeaderNode, Lang) + "\tm_pHeader;");
					JavaFile.WriteLine("public\tC" + Prefix + BuildClassName(TrailerNode, Lang) + "\tm_pTrailer;");
				}
			}

			DumpJSFieldsName(Class);


			if (Class.Name == "record" && Lang == Language.CPP)
			{
				if (Class.Attributes.GetNamedItem("SQLCreate") != null && Class.Attributes.GetNamedItem("SQLCreate").Value == "Y")
				{
					try
					{
						HFile.WriteLine("std::string\t&SQLTableName()\n{if(m_SQLTableName.length()==0)\nm_SQLTableName=\"" + Class.Attributes.GetNamedItem("SQLTable").Value + "\";\nreturn m_SQLTableName;\n}");
					}
					catch
					{
					}
				}
			}

			foreach (XmlNode Field in Class.SelectNodes("./field|./group"))
			{
				if (FieldExistsInSuperClass(Class, Field))
					continue;
				//				Field.Attributes.GetNamedItem("name").Value =	String.Concat(Field.Attributes.GetNamedItem("name").Value.Split(" \t".ToCharArray()));
				FindField(Field.Attributes.GetNamedItem("name").Value, Prefix);

				String FieldType = GetFieldType(Prefix, Field);

				if (Lang == Language.CPP)
					HFile.WriteLine(FieldType + "\t*m_p" + GetNodeID(Field) + "[2];");
				if (Lang == Language.Java)
				{
					JavaFile.WriteLine(FieldType + "\tm_p" + GetNodeID(Field) + ";");
					JavaFile.WriteLine("public\tstatic\tfinal\tString\t" + Prefix + "FieldName_" + Field.Attributes.GetNamedItem("name").Value + "\t=\t\"" + GetNodeID(Field) + "\";");
				}
				if (Lang == Language.JavaScript)
				{
					//					AllJSFieldsNames += CurrentJSShortScope + "." + GetNodeID(Field) + "={_name:'" + GetNodeID(Field) + "'};\n";
					//JSFile.WriteLine("this.m_p" + GetNodeID(Field) + "=null;");
					if (!bIsFirst)
					{
						JsonFields += ",{name: '" + GetNodeID(Field) + "'}";
					}
					else
					{
						JsonFields += "{name: '" + GetNodeID(Field) + "'}";
						bIsFirst = false;
					}
				}
				if (IsGroup(Field))
				{
					DumpClass(Prefix, Field, BuildJSGroupID(Field), Lang,bHTMLEncode);

					if (Lang == Language.CPP)
						HFile.WriteLine("C" + Prefix + BuildClassName(Field, Lang) + "\t**m_p" + BuildCPPGroupID(Field) + ";");

					if (Lang == Language.Java)
						JavaFile.WriteLine("C" + Prefix + BuildClassName(Field, Lang) + "[]\tm_p" + BuildJavaGroupID(Field) + ";");
					if (Lang == Language.JavaScript)
					{
						JSFile.WriteLine("this.m_p" + BuildJSGroupID(Field) + "=[];");
						JsonFields += ",{name: '" + BuildJSGroupID(Field) + "'}";
					}
				}
            }

            DumpConstructor(Prefix, Class, Lang);
			if (Lang == Language.JavaScript)
				JSFile.WriteLine("this.Constructor();");

			DumpDestructor(Prefix, Class, Lang);

			DumpImporter(Prefix, Class, Lang,bHTMLEncode);
			DumpExporter(Prefix, Class, Lang, false);
			if (Lang != Language.JavaScript)
			{
				DumpExporter(Prefix, Class, Lang, true);
			}            
            DumpSecureFieldForMessage(Lang);
			DumpAccessorFunctions(Prefix, Class, Lang);
			DumpSQLSupport(Prefix, Class, Lang);
			DumpRequiredFieldsChecker(Prefix, Class, Lang);
			if (Class.Name == "message" /*&& Class.Attributes.GetNamedItem("ExtendsMessage") != null*/)
			{
				DumpMessageImporter(Prefix, Class, Lang);
				DumpMessageExporter(Prefix, Class, Lang);
			}
			if (Lang == Language.CPP)
				HFile.WriteLine("};");
			if (Lang == Language.Java)
				JavaFile.WriteLine("}");
			if (Lang == Language.JavaScript)
				JSFile.WriteLine("};");

			if (Lang == Language.JavaScript)
			{
				CurrentJSScope = SavedJSScope;
				CurrentJSShortScope = SavedJSShortScope;

				JSFile.WriteLine(CurrentJSScope + "." + BuildJSClassName(Class) + ".prototype=new " + BaseJSClassName(Prefix, Class) + "();");
				JSFile.WriteLine(CurrentJSScope + "." + BuildJSClassName(Class) + ".prototype.constructor= " + CurrentJSScope + "." + BuildJSClassName(Class) + ";");
				JSFile.WriteLine(CurrentJSScope + "." + BuildJSClassName(Class) + ".superclass= " + BaseJSClassName(Prefix, Class) + ".prototype;");


				AllJSFieldsNames += CurrentJSShortScope + "." + BuildJSShortClassName(Class) + ".JSONFields=function(){return [" + JsonFields + "];};\n";
			}

			if (Class.Name == "record" && Class.Attributes.GetNamedItem("CreateList") != null && Class.Attributes.GetNamedItem("CreateList").Value == "Y")
			{
				XmlNode ListNode = Class.ParentNode.SelectSingleNode("./list[@name='" + Class.Attributes.GetNamedItem("name").Value + "']");
				if (ListNode == null)
				{
					XmlElement Node = Class.OwnerDocument.CreateElement("list");
					Node.Attributes.Append(Class.OwnerDocument.CreateAttribute("name"));
					Node.Attributes.GetNamedItem("name").Value = Class.Attributes.GetNamedItem("name").Value;
					if (Class.Attributes.GetNamedItem(Language.CPP.ToString()) != null)
					{
						Node.Attributes.Append(Class.OwnerDocument.CreateAttribute(Language.CPP.ToString()));
						Node.Attributes.GetNamedItem(Language.CPP.ToString()).Value = Class.Attributes.GetNamedItem(Language.CPP.ToString()).Value;
					}
					if (Class.Attributes.GetNamedItem(Language.Java.ToString()) != null)
					{
						Node.Attributes.Append(Class.OwnerDocument.CreateAttribute(Language.Java.ToString()));
						Node.Attributes.GetNamedItem(Language.Java.ToString()).Value = Class.Attributes.GetNamedItem(Language.Java.ToString()).Value;
					}
					if (Class.Attributes.GetNamedItem(Language.JavaScript.ToString()) != null)
					{
						Node.Attributes.Append(Class.OwnerDocument.CreateAttribute(Language.JavaScript.ToString()));
						Node.Attributes.GetNamedItem(Language.JavaScript.ToString()).Value = Class.Attributes.GetNamedItem(Language.JavaScript.ToString()).Value;
					}
					Class.ParentNode.InsertAfter(Node, Class);

					XmlNode List = Node;

					Node = Class.OwnerDocument.CreateElement("group");
					Node.Attributes.Append(Class.OwnerDocument.CreateAttribute("name"));
					Node.Attributes.GetNamedItem("name").Value = "EntriesCount";
					Node.Attributes.Append(Class.OwnerDocument.CreateAttribute("required"));
					Node.Attributes.GetNamedItem("required").Value = "N";
					Node.Attributes.Append(Class.OwnerDocument.CreateAttribute("groupid"));
					Node.Attributes.GetNamedItem("groupid").Value = "Entries";
					Node.Attributes.Append(Class.OwnerDocument.CreateAttribute("groupname"));
					Node.Attributes.GetNamedItem("groupname").Value = "Entries";
					Node.Attributes.Append(Class.OwnerDocument.CreateAttribute("ExtendsRecord"));
					Node.Attributes.GetNamedItem("ExtendsRecord").Value = Class.Attributes.GetNamedItem("name").Value;
					List.AppendChild(Node);
				}
			}
		}
		static void DumpFieldNumbers(string Prefix, XmlNodeList Fields, Language Lang)
		{
			if (Lang == Language.CPP)
			{
				HFile.WriteLine("enum __T" + Prefix + "TagID");
				HFile.WriteLine("{");
			}
			if (Lang == Language.JavaScript)
			{
				JSFile.WriteLine(TopMostJSScope + ".TagID={};");
			}

			//			int NextNumber = 5000;
			foreach (XmlNode Node in Fields)
			{
				if (Node.Attributes.GetNamedItem("unused") != null)
				{
					Node.Attributes.GetNamedItem("unused").Value = "Y";
				}
				else
				{
					Node.Attributes.Append(Node.OwnerDocument.CreateAttribute("unused"));
					Node.Attributes.GetNamedItem("unused").Value = "Y";
				}
				/*
				if (int.Parse(Node.Attributes.GetNamedItem("number").Value) > 1000)
				{
								Node.Attributes.GetNamedItem("number").Value = NextNumber.ToString();
								NextNumber++;
				}
				 */
				string FieldNumber = Node.Attributes.GetNamedItem("number").Value;
				if (Lang == Language.CPP)
				{
					HFile.WriteLine(Prefix + "TagID_" + Node.Attributes.GetNamedItem("name").Value + "\t=\t" + FieldNumber + ",");
				}

				if (Lang == Language.Java)
				{
					JavaFile.WriteLine("public\tstatic\tfinal\tint\t" + Prefix + "TagID_" + Node.Attributes.GetNamedItem("name").Value + "\t=\t" + FieldNumber + ";");
				}
				if (Lang == Language.JavaScript)
				{
					JSFile.WriteLine(TopMostJSScope + ".TagID." + Node.Attributes.GetNamedItem("name").Value + "\t=\t" + FieldNumber + ";");
				}

				if (FieldNumber == "35")	//	MessageType
				{
					if (Node.Attributes.GetNamedItem("type").Value == "INT")
					{
						bMsgTypeIsNumeric = true;
					}
				}
			}
			if (Lang == Language.CPP)
			{
				HFile.WriteLine(Prefix + "TagID_LAST");
				HFile.WriteLine("};");
			}
		}

		static int RemoveComponentElements(XmlNode Parent)
		{
			int ComponentsFound = 0;
			foreach (XmlNode Node in Parent.ChildNodes)
			{
				if (Node.Attributes != null && Node.Attributes.GetNamedItem("FromComponent") != null)
				{
					Node.ParentNode.RemoveChild(Node);
					ComponentsFound++;
				}
				else if (Node.HasChildNodes)
				{
					ComponentsFound += RemoveComponentElements(Node);
				}
			}
			return ComponentsFound;
		}


		enum TValueType
		{
			ValueTypeChar,
			ValueTypeString,
			ValueTypeBoolean,
			ValueTypeInt32,
			ValueTypeInt64
		};

		static void RenumberEnum(XmlNode Field)
		{
			int CurrentValue = 0;
			if (Field.Attributes != null && Field.Attributes.GetNamedItem("AutoGen") != null && Field.Attributes.GetNamedItem("AutoGen").Value == "Y")
			{
				Field.Attributes.GetNamedItem("AutoGen").Value = "N";
				XmlNodeList Values = Field.SelectNodes("./value");
				foreach (XmlNode Node in Values)
				{
					if (Node.Attributes.GetNamedItem("enum") == null)
						Node.Attributes.Append(Field.OwnerDocument.CreateAttribute("enum"));
					Node.Attributes.GetNamedItem("enum").Value = CurrentValue.ToString();
					CurrentValue++;

				}
			}
		}
		static void DumpEnumsArray(string Prefix)
		{
			XmlNodeList Fields = FixTop.SelectNodes("./fields/field");

			HFile.WriteLine("struct T" + Prefix + "AllEnumCodes{int EnumTagID;const char *TagName;const char *EnumName;const char *EnumCode;const char *EnumValue;};");
			CPPFile.WriteLine("T" + Prefix + "AllEnumCodes g_" + Prefix + "AllEnumsCodes[]={");

			foreach (XmlNode Field in Fields)
			{
				XmlNode BaseField = FindBaseField(Field);

				if (BaseField.HasChildNodes)
				{
					XmlNodeList Values = BaseField.SelectNodes("./value");
					if (Values.Count > 0)
					{
						foreach (XmlNode Value in Values)
						{
							string ValueText = "";
							if (Value.Attributes.GetNamedItem("text") != null)
							{
								ValueText = Value.Attributes.GetNamedItem("text").Value;
							}
							else
							{
								ValueText = Value.Attributes.GetNamedItem("description").Value;
							}


							CPPFile.Write("{" + Field.Attributes.GetNamedItem("number").Value + ",\"" + Field.Attributes.GetNamedItem("name").Value + "\",\"" + Value.Attributes.GetNamedItem("description").Value + "\",");
							CPPFile.Write("\"" + Value.Attributes.GetNamedItem("enum").Value + "\",");
							CPPFile.WriteLine("\"" + ValueText + "\"},");
						}
					}

				}
			}
			CPPFile.WriteLine("{0,NULL,NULL,NULL,NULL}};");
			CPPFile.WriteLine("T" + Prefix + "AllEnumCodes *GetAllEnumsCodes(){return g_" + Prefix + "AllEnumsCodes;}");
		}

		static string GetHibernateLength(XmlNode Field)
		{
			XmlNode FieldDef = FixTop.SelectSingleNode("/fix/fields/field[@name='" + Field.Attributes.GetNamedItem("name").Value + "']");

            while (DbNode.SelectSingleNode("./field[@name='" + FieldDef.Attributes.GetNamedItem("type").Value + "']") == null)
			{
				FieldDef = FixTop.SelectSingleNode("/fix/fields/field[@name='" + FieldDef.Attributes.GetNamedItem("type").Value + "']");
			}


			string HLength = "";
			try
			{
                if (FieldDef.Attributes.GetNamedItem("type") != null)
                {
                    XmlNode typeNode = DbNode.SelectSingleNode("./field[@name='" + FieldDef.Attributes.GetNamedItem("type").Value + "']");
                    if (typeNode.Attributes.GetNamedItem("HibernateLength") != null)
                    {
                        HLength = typeNode.Attributes.GetNamedItem("HibernateLength").Value;
                        return "length=\"" + HLength + "\"";
                    }
                }
			}
			catch
			{
			}
			return "";
		}

		static string GetHibernateType(XmlNode Field, bool isEncrypted, bool isUnique)
        {
            try
            {
                XmlNode FieldDef = FixTop.SelectSingleNode("/fix/fields/field[@name='" + Field.Attributes.GetNamedItem("name").Value + "']");

                while (DbNode.SelectSingleNode("./field[@name='" + FieldDef.Attributes.GetNamedItem("type").Value + "']") == null)
                {
                    FieldDef = FixTop.SelectSingleNode("/fix/fields/field[@name='" + FieldDef.Attributes.GetNamedItem("type").Value + "']");
                }


                string HType = "";
                string unique = "";
                try
                {
                    XmlNode PrimFieldNode = DbNode.SelectSingleNode("./field[@name='" + FieldDef.Attributes.GetNamedItem("type").Value + "']");
                    
                    HType = PrimFieldNode.Attributes.GetNamedItem("HibernateType").Value;
                    if (isEncrypted)
                    {
                        if (PrimFieldNode.Attributes.GetNamedItem("EncryptedHibernateType") == null || PrimFieldNode.Attributes.GetNamedItem("EncryptedHibernateType").Value == null
                        || PrimFieldNode.Attributes.GetNamedItem("EncryptedHibernateType").Value.Length <= 0)
                        {
                            Console.Error.WriteLine("ERROR::Encryption Type not defined for HibernateType: " + HType);
                            Environment.Exit(1);
                        }
                        if (isUnique)
                        {
                            unique = "unique";
                        }
                        else
                        {
                            unique = "";
                        }
                        HType = unique + PrimFieldNode.Attributes.GetNamedItem("EncryptedHibernateType").Value;
                    }
                }
                catch
                {
                }
                return HType;
            }
            catch (Exception)
            {
                Console.WriteLine("Failed to process field (" + Field.OuterXml + ")");
                throw;
            }
		}

		static void DumpEnumMap(string Prefix, XmlNode Field, Language Lang, bool bReverse, string TextLanguage, string AccessMethod, string CompanyName)
		{
			String Key = "int";
			String Value = "std::string";
			TValueType ValueType = TValueType.ValueTypeString;

			string BaseType = FindPrimitiveType(Field.Attributes.GetNamedItem("type").Value);

			if (BaseType == "CHAR")
				ValueType = TValueType.ValueTypeChar;
			else
				if (BaseType == "STRING" || BaseType == "MULTIPLEVALUESTRING")
					ValueType = TValueType.ValueTypeString;
				else
					if (BaseType == "INT")
						ValueType = TValueType.ValueTypeInt32;
					else
						if (BaseType == "INT32")
							ValueType = TValueType.ValueTypeInt32;
						else
							if (BaseType == "INT64")
								ValueType = TValueType.ValueTypeInt64;
							else
								if (BaseType == "BOOLEAN")
									ValueType = TValueType.ValueTypeBoolean;

			if (ValueType == TValueType.ValueTypeChar)
				Key = "char";
			else
				if (ValueType == TValueType.ValueTypeString)
					Key = "std::string";
				else
					if (ValueType == TValueType.ValueTypeInt32)
						Key = "int32_t";
					else
						if (ValueType == TValueType.ValueTypeInt32)
							Key = "int32_t";
						else
							if (ValueType == TValueType.ValueTypeInt64)
								Key = "int32_t";
							else
								if (ValueType == TValueType.ValueTypeBoolean)
									Key = "bool";
			string TypeString;
			if (bReverse)
				TypeString = "<" + Value + "," + Key + ">";
			else
				TypeString = "<" + Key + "," + Value + ">";

			if (Lang == Language.CPP)
			{
                HFile.WriteLine("class EXPORTABLE_FIX_API	C" + Prefix + (bReverse ? "rmap_" : "map_") + Field.Attributes.GetNamedItem("name").Value + TextLanguage + AccessMethod + CompanyName + "\t:\tpublic\tEXPORTABLE_STL::map" + TypeString);
				HFile.WriteLine("{");
				HFile.WriteLine("public:");
                HFile.WriteLine("\tstatic\tC" + Prefix + (bReverse ? "rmap_" : "map_") + Field.Attributes.GetNamedItem("name").Value + TextLanguage + AccessMethod + CompanyName + "\t*GetMap();");
                HFile.WriteLine("\tC" + Prefix + (bReverse ? "rmap_" : "map_") + Field.Attributes.GetNamedItem("name").Value + TextLanguage + AccessMethod + CompanyName + "();");
				HFile.WriteLine("};");
                CPPFile.WriteLine("C" + Prefix + (bReverse ? "rmap_" : "map_") + Field.Attributes.GetNamedItem("name").Value + TextLanguage + AccessMethod + CompanyName + "::C" + Prefix + (bReverse ? "rmap_" : "map_") + Field.Attributes.GetNamedItem("name").Value + TextLanguage + AccessMethod + CompanyName + "()\t:");
				CPPFile.WriteLine("\tEXPORTABLE_STL::map" + TypeString + "()");
				CPPFile.WriteLine("{");
			}


			foreach (XmlNode Node in Field.SelectNodes("./value"))
			{
				string ItemText = null;
                if (TextLanguage != "" && AccessMethod != "" && CompanyName != "")
				{
                    var TextNode = Node.SelectSingleNode("./" + CompanyName + "/" + TextLanguage + "/" + AccessMethod + "/Text");
					if (TextNode != null)
						ItemText = TextNode.InnerXml;
				}
				/*
				if(OldTextNode	==	null && AccessMethod	==	"SMS")
					OldTextNode = Node.SelectSingleNode("./" + TextLanguage + "Text");
				if (OldTextNode != null)
				{
					var LangNode = Node.SelectSingleNode("./" + TextLanguage);
					if (LangNode == null)
					{
						LangNode = Node.OwnerDocument.CreateElement(TextLanguage);
						Node.AppendChild(LangNode);
					}
					var MethodNode = LangNode.SelectSingleNode("./" + AccessMethod);
					if (MethodNode == null)
					{
						MethodNode = Node.OwnerDocument.CreateElement(AccessMethod);
						LangNode.AppendChild(MethodNode);
					}
					var TextNode=MethodNode.SelectSingleNode("./Text");
					if (TextNode == null)
					{
						TextNode = Node.OwnerDocument.CreateElement("Text");
						MethodNode.AppendChild(TextNode);
					}
					TextNode.InnerXml = OldTextNode.InnerXml;
					Node.RemoveChild(OldTextNode);
					ItemText = TextNode.InnerXml;
				}
				*/
				if (bReverse)
				{
					if (Lang == Language.CPP)
						CPPFile.WriteLine("\t(*this)[\"" + Node.Attributes.GetNamedItem("description").Value + "\"]\t=\t" + Prefix + Field.Attributes.GetNamedItem("name").Value + "_" + Node.Attributes.GetNamedItem("description").Value + ";");
				}
				else
				{
					if (ItemText != null)
					{
						if (Lang == Language.CPP)
						{
							string FlashOnly = (Node.Attributes.GetNamedItem("FlashOnly") != null && Node.Attributes.GetNamedItem("FlashOnly").Value.ToLower() == "y") ? "$(FlashOnly)" : "";
                            var STKML = Node.SelectSingleNode("./" + CompanyName + "/" + TextLanguage + "/" + AccessMethod + "/stkml");
                            var HTML = Node.SelectSingleNode("./" + CompanyName + "/" + TextLanguage + "/" + AccessMethod + "/html");
                            var HTMLBody = Node.SelectSingleNode("./" + CompanyName + "/" + TextLanguage + "/" + AccessMethod + "/htmlbody");

							string TextToSet = "";
							if (STKML != null || HTML != null || HTMLBody != null)
							{
								if (STKML != null)
								{
									var StkmlText = STKML.OuterXml;
									StkmlText = StkmlText.Replace("$(Text)", ItemText);
									StkmlText = StkmlText.Replace("\"", "\\\"");
									TextToSet = StkmlText;
								}
								if (HTML != null)
								{
									var HtmlText = HTML.OuterXml;
									HtmlText = HtmlText.Replace("$(Text)", ItemText);
									HtmlText = HtmlText.Replace("\"", "\\\"");
									TextToSet += HtmlText;
								}
								if (HTMLBody != null)
								{
									var HtmlBodyText = HTMLBody.OuterXml;
									HtmlBodyText = HtmlBodyText.Replace("$(Text)", ItemText);
									HtmlBodyText = HtmlBodyText.Replace("\"", "\\\"");
									TextToSet += HtmlBodyText;
								}

								TextToSet = TextToSet.Replace("\r", "");
								TextToSet = TextToSet.Replace("\n", "");
								CPPFile.WriteLine("\t(*this)[" + Prefix + Field.Attributes.GetNamedItem("name").Value + "_" + Node.Attributes.GetNamedItem("description").Value + "]\t=\tstd::string(\"" + TextToSet + FlashOnly + "\");");
							}
							else
							{
								CPPFile.WriteLine("\t(*this)[" + Prefix + Field.Attributes.GetNamedItem("name").Value + "_" + Node.Attributes.GetNamedItem("description").Value + "]\t=\tstd::string(\"" + ItemText + FlashOnly + "\");");
							}
						}
					}
					else
					{
						if (Lang == Language.CPP && TextLanguage == "" && AccessMethod == "")
							CPPFile.WriteLine("\t(*this)[" + Prefix + Field.Attributes.GetNamedItem("name").Value + "_" + Node.Attributes.GetNamedItem("description").Value + "]\t=\tstd::string(\"" + Node.Attributes.GetNamedItem("description").Value + "\");");
					}
				}
			}
			if (Lang == Language.CPP)
			{
				CPPFile.WriteLine("}");
                CPPFile.WriteLine("static\tC" + Prefix + (bReverse ? "rmap_" : "map_") + Field.Attributes.GetNamedItem("name").Value + TextLanguage + AccessMethod + CompanyName + "\tg" + (bReverse ? "rmap_" : "map_") + Field.Attributes.GetNamedItem("name").Value + TextLanguage + AccessMethod + CompanyName + ";");
                CPPFile.WriteLine("C" + Prefix + (bReverse ? "rmap_" : "map_") + Field.Attributes.GetNamedItem("name").Value + TextLanguage + AccessMethod + CompanyName + "\t*C" + Prefix + (bReverse ? "rmap_" : "map_") + Field.Attributes.GetNamedItem("name").Value + TextLanguage + AccessMethod + CompanyName + "::GetMap()");
				CPPFile.WriteLine("{");
                CPPFile.WriteLine("\treturn\t&g" + (bReverse ? "rmap_" : "map_") + Field.Attributes.GetNamedItem("name").Value + TextLanguage + AccessMethod + CompanyName + ";");
				CPPFile.WriteLine("}");
			}
		}
		static void DumpISO8583Elements(string Prefix, Language Lang)
		{
			if (Lang != Language.CPP)
				return;
			foreach (XmlNode Field in FixTop.SelectNodes("./fields/field"))
			{
				if (Field.Attributes.GetNamedItem("CreateElements") == null)
					continue;
				if (Field.Attributes.GetNamedItem("CreateElements").Value.ToLower() != "y")
					continue;
				if (Field.Attributes.GetNamedItem("ElementsClass") == null)
					continue;
				if (Field.Attributes.GetNamedItem("EntriesName") == null)
					continue;

				string EntriesName = Field.Attributes.GetNamedItem("EntriesName").Value;
				XmlNode Class = FixTop.SelectSingleNode("./classes/class[@name='" + Field.Attributes.GetNamedItem("ElementsClass").Value + "']");
				if (Class == null)
					continue;

				HFile.WriteLine("void\tEXPORTABLE_FIX_API\tSet_" + Prefix + Field.Attributes.GetNamedItem("name").Value + "(C" + Prefix + BuildClassName(Class, Lang) + " &Elements);");
				CPPFile.WriteLine("void\tSet_" + Prefix + Field.Attributes.GetNamedItem("name").Value + "(C" + Prefix + BuildClassName(Class, Lang) + " &Elements)");
				CPPFile.WriteLine("{");
				CPPFile.WriteLine(GenerateClassHierarchy(Prefix, Class, Lang) + "::C" + Prefix + "group_" + EntriesName + "**pEntries=Elements.Allocate_" + EntriesName + "(129);");

				foreach (XmlNode Node in Field.SelectNodes("./value"))
				{
					string Entry = "pEntries[" + Prefix + Field.Attributes.GetNamedItem("name").Value + "_" + Node.Attributes.GetNamedItem("description").Value + "]";
					CPPFile.WriteLine(Entry + "=new " + GenerateClassHierarchy(Prefix, Class, Lang) + "::C" + Prefix + "group_" + EntriesName + "();");
					CPPFile.WriteLine(Entry + "->SetIDValue(" + Prefix + Field.Attributes.GetNamedItem("name").Value + "_" + Node.Attributes.GetNamedItem("description").Value + ");");
					if (Node.Attributes.GetNamedItem("Type") != null)
						CPPFile.WriteLine(Entry + "->SetTypeValue(" + Prefix + "ISO8583ElementType_" + Node.Attributes.GetNamedItem("Type").Value + ");");

					if (Node.Attributes.GetNamedItem("VarSize") != null)
						CPPFile.WriteLine(Entry + "->SetVarSizeValue(" + Node.Attributes.GetNamedItem("VarSize").Value + ");");
					if (Node.Attributes.GetNamedItem("Size") != null)
						CPPFile.WriteLine(Entry + "->SetSizeValue(" + Node.Attributes.GetNamedItem("Size").Value + ");");
					if (Node.Attributes.GetNamedItem("SizeDigits") != null)
						CPPFile.WriteLine(Entry + "->SetSizeDigitsValue(" + Node.Attributes.GetNamedItem("SizeDigits").Value + ");");
					if (Node.Attributes.GetNamedItem("Pad") != null && Node.Attributes.GetNamedItem("Pad").Value.ToLower() == "y")
						CPPFile.WriteLine(Entry + "->SetPadValue(1);");
					if (Node.Attributes.GetNamedItem("Hex") != null && Node.Attributes.GetNamedItem("Hex").Value.ToLower() == "y")
						CPPFile.WriteLine(Entry + "->SetHexValue(1);");
					if (Node.Attributes.GetNamedItem("Left") != null && Node.Attributes.GetNamedItem("Left").Value.ToLower() == "y")
						CPPFile.WriteLine(Entry + "->SetLeftValue(1);");
				}
				CPPFile.WriteLine("}");
			}
		}

		static void DumpEnum(string Prefix, XmlNode Field, Language Lang)
		{
			TValueType ValueType = TValueType.ValueTypeString;
			RenumberEnum(Field);

			string BaseType = FindPrimitiveType(Field.Attributes.GetNamedItem("type").Value);

			if (BaseType == "CHAR")
				ValueType = TValueType.ValueTypeChar;
			else
				if (BaseType == "STRING" || BaseType == "MULTIPLEVALUESTRING")
					ValueType = TValueType.ValueTypeString;
				else
					if (BaseType == "INT")
						ValueType = TValueType.ValueTypeInt32;
					else
						if (BaseType == "INT32")
							ValueType = TValueType.ValueTypeInt32;
						else
							if (BaseType == "INT64")
								ValueType = TValueType.ValueTypeInt64;
							else
								if (BaseType == "BOOLEAN")
									ValueType = TValueType.ValueTypeBoolean;

			if (Lang == Language.JavaScript)
				JSFile.WriteLine(TopMostJSScope + "." + Field.Attributes.GetNamedItem("name").Value + "={};");

			foreach (XmlNode Node in Field.SelectNodes("./value"))
			{
				Node.Attributes.GetNamedItem("description").Value = Node.Attributes.GetNamedItem("description").Value.Replace(' ', '_');
				Node.Attributes.GetNamedItem("description").Value = Node.Attributes.GetNamedItem("description").Value.Replace('\t', '_');
				if (Node.Attributes.GetNamedItem("text") != null)
				{
					if (Lang == Language.CPP)
						HFile.WriteLine("#define\t" + Prefix + Field.Attributes.GetNamedItem("name").Value + "_" + Node.Attributes.GetNamedItem("description").Value + "_Text\t\"" + Node.Attributes.GetNamedItem("text").Value + "\"");
					if (Lang == Language.Java)
						JavaFile.WriteLine("public\tstatic\tfinal\tString\t" + Prefix + Field.Attributes.GetNamedItem("name").Value + "_" + Node.Attributes.GetNamedItem("description").Value + "_Text\t=\t\"" + Node.Attributes.GetNamedItem("text").Value + "\";");
					if (Lang == Language.JavaScript)
						JSFile.WriteLine(TopMostJSScope + "." + Field.Attributes.GetNamedItem("name").Value + "." + Node.Attributes.GetNamedItem("description").Value + "_Text\t=\t\"" + Node.Attributes.GetNamedItem("text").Value + "\";");
				}
				if (Lang == Language.CPP)
				{
					if (ValueType == TValueType.ValueTypeChar)
						HFile.WriteLine("#define\t" + Prefix + Field.Attributes.GetNamedItem("name").Value + "_" + Node.Attributes.GetNamedItem("description").Value + "\t'" + Node.Attributes.GetNamedItem("enum").Value + "'");
					else
						if (ValueType == TValueType.ValueTypeString)
							HFile.WriteLine("#define\t" + Prefix + Field.Attributes.GetNamedItem("name").Value + "_" + Node.Attributes.GetNamedItem("description").Value + "\t\"" + Node.Attributes.GetNamedItem("enum").Value + "\"");
						else
							if (ValueType == TValueType.ValueTypeInt32)
								HFile.WriteLine("#define\t" + Prefix + Field.Attributes.GetNamedItem("name").Value + "_" + Node.Attributes.GetNamedItem("description").Value + "\t(int32_t)" + Node.Attributes.GetNamedItem("enum").Value);
							else
								if (ValueType == TValueType.ValueTypeInt64)
									HFile.WriteLine("#define\t" + Prefix + Field.Attributes.GetNamedItem("name").Value + "_" + Node.Attributes.GetNamedItem("description").Value + "\t(int64_t)" + Node.Attributes.GetNamedItem("enum").Value);
								else
									if (ValueType == TValueType.ValueTypeBoolean)
										HFile.WriteLine("#define\t" + Prefix + Field.Attributes.GetNamedItem("name").Value + "_" + Node.Attributes.GetNamedItem("description").Value + "\t" + Node.Attributes.GetNamedItem("enum").Value);
				}
				if (Lang == Language.Java)
				{
					if (ValueType == TValueType.ValueTypeChar)
						JavaFile.WriteLine("public\tstatic\tfinal\tCharacter\t" + Prefix + Field.Attributes.GetNamedItem("name").Value + "_" + Node.Attributes.GetNamedItem("description").Value + "\t=\t'" + Node.Attributes.GetNamedItem("enum").Value + "';");
					else
						if (ValueType == TValueType.ValueTypeString)
							JavaFile.WriteLine("public\tstatic\tfinal\tString\t" + Prefix + Field.Attributes.GetNamedItem("name").Value + "_" + Node.Attributes.GetNamedItem("description").Value + "\t=\t\"" + Node.Attributes.GetNamedItem("enum").Value + "\";");
						else
							if (ValueType == TValueType.ValueTypeInt32)
								JavaFile.WriteLine("public\tstatic\tfinal\tInteger\t" + Prefix + Field.Attributes.GetNamedItem("name").Value + "_" + Node.Attributes.GetNamedItem("description").Value + "\t=\t" + Node.Attributes.GetNamedItem("enum").Value + ";");
							else
								if (ValueType == TValueType.ValueTypeInt64)
									JavaFile.WriteLine("public\tstatic\tfinal\tLong\t" + Prefix + Field.Attributes.GetNamedItem("name").Value + "_" + Node.Attributes.GetNamedItem("description").Value + "\t=\t" + Node.Attributes.GetNamedItem("enum").Value + "L;");
								else
									if (ValueType == TValueType.ValueTypeBoolean)
										JavaFile.WriteLine("public\tstatic\tfinal\tBoolean\t" + Prefix + Field.Attributes.GetNamedItem("name").Value + "_" + Node.Attributes.GetNamedItem("description").Value + "\t=\t" + Node.Attributes.GetNamedItem("enum").Value + ";");
				}
				if (Lang == Language.JavaScript)
				{
					if (ValueType == TValueType.ValueTypeChar)
						JSFile.WriteLine(TopMostJSScope + "." + Field.Attributes.GetNamedItem("name").Value + "." + Node.Attributes.GetNamedItem("description").Value + "\t=\t'" + Node.Attributes.GetNamedItem("enum").Value + "';");
					else
						if (ValueType == TValueType.ValueTypeString)
							JSFile.WriteLine(TopMostJSScope + "." + Field.Attributes.GetNamedItem("name").Value + "." + Node.Attributes.GetNamedItem("description").Value + "\t=\t\"" + Node.Attributes.GetNamedItem("enum").Value + "\";");
						else
							if (ValueType == TValueType.ValueTypeInt32)
								JSFile.WriteLine(TopMostJSScope + "." + Field.Attributes.GetNamedItem("name").Value + "." + Node.Attributes.GetNamedItem("description").Value + "\t=\t" + Node.Attributes.GetNamedItem("enum").Value + ";");
							else
								if (ValueType == TValueType.ValueTypeInt64)
									JSFile.WriteLine(TopMostJSScope + "." + Field.Attributes.GetNamedItem("name").Value + "." + Node.Attributes.GetNamedItem("description").Value + "\t=\t" + Node.Attributes.GetNamedItem("enum").Value + ";");
								else
									if (ValueType == TValueType.ValueTypeBoolean)
										JSFile.WriteLine(TopMostJSScope + "." + Field.Attributes.GetNamedItem("name").Value + "." + Node.Attributes.GetNamedItem("description").Value + "\t=\t" + Node.Attributes.GetNamedItem("enum").Value + ";");
				}
			}


			if (Field.Attributes.GetNamedItem("Map") != null && Field.Attributes.GetNamedItem("Map").Value.ToLower() == "y")
			{
				string[] Texts = { };
				string[] Methods = { };
                string[] CompanyNames = { };
				if (Field.Attributes.GetNamedItem("Language") != null)
					Texts = Field.Attributes.GetNamedItem("Language").Value.Split(", \t".ToCharArray());
				if (Field.Attributes.GetNamedItem("Method") != null)
                    Methods = Field.Attributes.GetNamedItem("Method").Value.Split(", \t".ToCharArray());
                if (Field.Attributes.GetNamedItem("CompanyIDs") != null)
                    CompanyNames = Field.Attributes.GetNamedItem("CompanyIDs").Value.Split(", \t".ToCharArray());

				foreach (string Txt in Texts)
				{
                    foreach (string Method in Methods)
                    {
                        foreach (string CompanyName in CompanyNames)
                            DumpEnumMap(Prefix, Field, Lang, false, Txt, Method, CompanyName);
                        DumpEnumMap(Prefix, Field, Lang, false, Txt, Method, "");
                    }
				}
				DumpEnumMap(Prefix, Field, Lang, false, "", "", "");

			}
			if (Field.Attributes.GetNamedItem("ReverseMap") != null && Field.Attributes.GetNamedItem("ReverseMap").Value.ToLower() == "y")
			{
				DumpEnumMap(Prefix, Field, Lang, true, "","", "");
			}
		}


		static String FindBaseType(String FieldType)
		{
			if (DefinesTable.ContainsKey(FieldType))
			{
				return (String)DefinesTable[FieldType];
			}
			XmlNodeList Fields = FixTop.SelectNodes("./fields/field");
			for (int I = 0; I < Fields.Count; I++)
			{
				XmlNode Node = Fields[I];
				if (Node.Attributes.GetNamedItem("name").Value == FieldType)
				{
					Node.Attributes.RemoveNamedItem("unused");
					return FindBaseType(Node.Attributes.GetNamedItem("type").Value);
				}
			}
			return FieldType;
		}
		static String FindPrimitiveType(String FieldType)
		{
			if (DbNode.SelectSingleNode("./field[@name='" + FieldType + "']") != null)
				return FieldType;

			XmlNodeList Fields = FixTop.SelectNodes("./fields/field");
			for (int I = 0; I < Fields.Count; I++)
			{
				XmlNode Node = Fields[I];
				if (Node.Attributes.GetNamedItem("name").Value == FieldType)
				{
					Node.Attributes.RemoveNamedItem("unused");
					return FindPrimitiveType(Node.Attributes.GetNamedItem("type").Value);
				}
			}
			return FieldType;
		}

		public static XmlNode FindBaseField(XmlNode Field)
		{
			XmlNode Base = FixTop.SelectSingleNode("./fields/field[@name='" + Field.Attributes.GetNamedItem("type").Value + "']");
			if (Base == null)
				return Field;
			return FindBaseField(Base);
		}

		static void SetMessageTypes(string Prefix, XmlNodeList Messages, Language Lang)
		{

			XmlNode MsgTypeNode = FixTop.SelectSingleNode("./fields/field[@number='35']");
			while (MsgTypeNode.HasChildNodes)
				MsgTypeNode.RemoveChild(MsgTypeNode.FirstChild);

			if (Lang == Language.JavaScript)
				JSFile.WriteLine(TopMostJSScope + ".MessageType={};");

			int LastMsgType = 1000;
			int MsgType = 0;
			for (int I = 0; I < Messages.Count; I++)
			{
				if (Messages[I].Attributes.GetNamedItem("msgtype") != null && Messages[I].Attributes.GetNamedItem("msgtype").Value == "AUTO")
				{
					for (int J = 0; J < Messages.Count; J++)
					{
						if (Messages[J].Attributes.GetNamedItem("msgtype") != null && Messages[J].Attributes.GetNamedItem("msgtype").Value != "AUTO")
						{
							try
							{
								MsgType = int.Parse(Messages[J].Attributes.GetNamedItem("msgtype").Value);
								if (MsgType > LastMsgType)
								{
									LastMsgType = MsgType;
								}
							}
							catch
							{
							}
						}
					}
					Messages[I].Attributes.GetNamedItem("msgtype").Value = (LastMsgType + 1).ToString();
				}
				if (Messages[I].Attributes.GetNamedItem("msgtype") != null)
				{
					XmlElement Node = MsgTypeNode.OwnerDocument.CreateElement("value");
					Node.Attributes.Append(MsgTypeNode.OwnerDocument.CreateAttribute("enum"));
					Node.Attributes.GetNamedItem("enum").Value = Messages[I].Attributes.GetNamedItem("msgtype").Value;
					Node.Attributes.Append(MsgTypeNode.OwnerDocument.CreateAttribute("description"));
					Node.Attributes.GetNamedItem("description").Value = Messages[I].Attributes.GetNamedItem("name").Value;
					MsgTypeNode.AppendChild(Node);


					if (bMsgTypeIsNumeric)
					{
						if (Lang == Language.CPP)
							HFile.WriteLine("#define\t" + Prefix + "MessageType_" + Messages[I].Attributes.GetNamedItem("name").Value + "\t" + Messages[I].Attributes.GetNamedItem("msgtype").Value);
						if (Lang == Language.Java)
							JavaFile.WriteLine("public\tstatic\tfinal\tint\t" + Prefix + "MessageType_" + Messages[I].Attributes.GetNamedItem("name").Value + "\t=\t" + Messages[I].Attributes.GetNamedItem("msgtype").Value + ";");
						if (Lang == Language.JavaScript)
							JSFile.WriteLine(TopMostJSScope + ".MessageType." + Messages[I].Attributes.GetNamedItem("name").Value + "\t=\t" + Messages[I].Attributes.GetNamedItem("msgtype").Value + ";");
					}
					else
					{
						if (Lang == Language.CPP)
							HFile.WriteLine("#define\t" + Prefix + "MessageType_" + Messages[I].Attributes.GetNamedItem("name").Value + "\t\"" + Messages[I].Attributes.GetNamedItem("msgtype").Value + "\"");
						if (Lang == Language.Java)
							JavaFile.WriteLine("public\tstatic\tfinal\tString\t" + Prefix + "MessageType_" + Messages[I].Attributes.GetNamedItem("name").Value + "\t=\t\"" + Messages[I].Attributes.GetNamedItem("msgtype").Value + "\";");
						if (Lang == Language.JavaScript)
							JSFile.WriteLine(TopMostJSScope + ".MessageType." + Messages[I].Attributes.GetNamedItem("name").Value + "\t=\t\"" + Messages[I].Attributes.GetNamedItem("msgtype").Value + "\";");
					}
				}
			}
		}


		static void SetFieldDefs(String Prefix, XmlNodeList Fields, Language Lang)
		{

			for (int I = 0; I < Fields.Count; I++)
			{
				XmlNode Node = Fields[I];
				String FieldType = FindBaseType(Node.Attributes.GetNamedItem("type").Value);
				String TypeDefName = "T" + Prefix + Node.Attributes.GetNamedItem("name").Value;
				TypedefsTable[TypeDefName] = FieldType;
				if (Lang == Language.CPP)
					HFile.WriteLine("typedef\t" + FieldType + "\t" + TypeDefName + ";");
				if (Node.HasChildNodes)
				{
					DumpEnum(Prefix, Node, Lang);
				}
			}
		}

		static void DumpFieldDefs(string Prefix, XmlNode Fields, Language Lang)
		{
			foreach (XmlNode Node in Fields.SelectNodes("./fields"))
			{
				if (Lang == Language.CPP)
					HFile.WriteLine("typedef\tT" + Prefix + Node.Attributes.GetNamedItem("type").Value + "\tT" + Prefix + Node.Attributes.GetNamedItem("name").Value + ";");
				if (Node.HasChildNodes)
					DumpEnum(Prefix, Node, Lang);
			}
		}


		static void DumpDerivedMessagesConstructor(string Prefix, Language Lang)
		{
			if (Lang == Language.CPP)
			{
				if (bMsgTypeIsNumeric)
					CPPFile.WriteLine("CFIXMsg\t*" + Prefix + "MessageCreator(Integer\t&MsgTypeParam)");
				else
					CPPFile.WriteLine("CFIXMsg\t*" + Prefix + "MessageCreator(std::string\t&MsgType)");
				CPPFile.WriteLine("{");

				if (bMsgTypeIsNumeric)
				{
					CPPFile.WriteLine("int\tMsgType\t=\tMsgTypeParam;");
				}
				foreach (XmlNode Node in FixTop.SelectNodes("/fix/messages/message"))
				{
					try
					{
						if (Node.Attributes.GetNamedItem(Language.CPP.ToString()).Value == "Y" && Node.Attributes.GetNamedItem("msgtype") != null)
						{
							CPPFile.WriteLine("if(MsgType\t==\t" + Prefix + "MessageType_" + Node.Attributes.GetNamedItem("name").Value + ")\treturn\tnew\t" + "C" + Prefix + BuildClassName(Node, Lang) + "();");
						}
					}
					catch
					{
					}
				}
				CPPFile.WriteLine("return\tNULL;");
				CPPFile.WriteLine("};");
				CPPFile.WriteLine("bool\tgbFIXMessageCreatorSet\t=\tCFIXMsg::SetFIXMsgCreator(" + Prefix + "MessageCreator);");
			}
			if (Lang == Language.Java)
			{
				JavaFile.WriteLine("public\tstatic\tclass\tC" + Prefix + "MessageCreator\timplements\tIFIXMessageCreator{");

				if (bMsgTypeIsNumeric)
				{
					JavaFile.WriteLine("public\tCFIXMsg\tCreate(Integer\tMsgType){");
				}
				else
				{
					JavaFile.WriteLine("public\tCFIXMsg\tCreate(String\tMsgType){");
				}

				JavaFile.WriteLine("switch(MsgType)");
				JavaFile.WriteLine("{");

				foreach (XmlNode Node in FixTop.SelectNodes("/fix/messages/message"))
				{
					try
					{
						if (Node.Attributes.GetNamedItem(Language.Java.ToString()).Value.CompareTo("Y") == 0 && Node.Attributes.GetNamedItem("msgtype") != null)
						{
							JavaFile.WriteLine("case\t" + Prefix + "MessageType_" + Node.Attributes.GetNamedItem("name").Value + "\t:\treturn\tnew\t" + "C" + Prefix + BuildClassName(Node, Lang) + "();");
						}
					}
					catch
					{
					}
				}
				JavaFile.WriteLine("}");
				JavaFile.WriteLine("return\tnull;");
				JavaFile.WriteLine("}");
				JavaFile.WriteLine("}");
			}
			if (Lang == Language.JavaScript)
			{
				JSFile.WriteLine(CurrentJSScope + ".MessageCreator=function(){");

				JSFile.WriteLine("this.Create\t=\tfunction(MsgType){");

				JSFile.WriteLine("switch(MsgType)");
				JSFile.WriteLine("{");

				foreach (XmlNode Node in FixTop.SelectNodes("/fix/messages/message"))
				{
					try
					{
						if (Node.Attributes.GetNamedItem(Language.JavaScript.ToString()).Value.CompareTo("Y") == 0 && Node.Attributes.GetNamedItem("msgtype") != null)
						{
							JSFile.WriteLine("case\t" + TopMostJSScope + ".MessageType." + Node.Attributes.GetNamedItem("name").Value + "\t:\treturn\tnew\t" + TopMostJSScope + "." + BuildJSClassName(Node) + "();");
						}
					}
					catch
					{
					}
				}

				JSFile.WriteLine("}");
				JSFile.WriteLine("return\tnull;");
				JSFile.WriteLine("};");
				JSFile.WriteLine("};");
				JSFile.WriteLine("FIX.MessageCreator	=	new " + CurrentJSScope + ".MessageCreator();");
			}
		}
		static void MergeComponents(XmlNode Node)
		{
			foreach (XmlNode Field in Node)
			{
				if (Field.Name == "group")
					MergeComponents(Field);
				else if (Field.Name == "component")
				{
					XmlNode Comp = FixTop.SelectSingleNode("/fix/components/component[@name='" + Field.Attributes.GetNamedItem("name").Value + "']");
					XmlNode LastNode = Field;
					string Required = Field.Attributes.GetNamedItem("required").Value;
					foreach (XmlNode CompField in Comp)
					{
						if (CompField.Name == "#comment")
							continue;
						LastNode = Field.ParentNode.InsertAfter(CompField.Clone(), LastNode);
						LastNode.Attributes.Append(Field.OwnerDocument.CreateAttribute("FromComponent"));
						if (Required == "N" && LastNode.Attributes.GetNamedItem("required").Value == "Y")
							LastNode.Attributes.GetNamedItem("required").Value = "N";
					}
				}
			}
		}
		
		static void DumpJSImporter(string Prefix, Language Lang){
			if (Lang == Language.JavaScript)
					{
						JSFile.WriteLine("C"+Prefix+".ImportMsg=function(Buf)");

						JSFile.WriteLine("{");
						JSFile.WriteLine("var\tParams=new FIX.CFIXMsgParseParams();");
						JSFile.WriteLine("this.Header().Import(Buf,Params);");
						JSFile.WriteLine("if(Params.TagOffset\t>=\tBuf.Length())");
						JSFile.WriteLine("{return\tfalse;}");

						JSFile.WriteLine("this.Import(Buf,Params);");
						JSFile.WriteLine("if(Params.TagOffset\t>=\tBuf.Length())");
						JSFile.WriteLine("{return\tfalse;}");

						JSFile.WriteLine("if(!this.Trailer().Import(Buf,Params))");
						JSFile.WriteLine("{return\tfalse;}");
						JSFile.WriteLine("return\ttrue;");
						JSFile.WriteLine("};");
					}
		}
		
		static void DumpJSExporter(string Prefix, Language Lang){
			if (Lang == Language.JavaScript)
					{
						JSFile.WriteLine("C"+Prefix+".ToFIX\t=\tfunction(Buf)");
						JSFile.WriteLine("{");
						JSFile.WriteLine("this.Header().Export(Buf);");
						JSFile.WriteLine("this.Export(Buf);");
						JSFile.WriteLine("this.Trailer().Export(Buf);");
						JSFile.WriteLine("this.AdjustLengthAndChecksum(Buf);");
						JSFile.WriteLine("this.CloneRawData(Buf);");
						JSFile.WriteLine("};");
					}
		}

		static void Main(string[] args)
		{
			string Dir = ".";
			string Prefix = "mFinoFIX";
			string FixmlFile = "A:\\MFS_V2_5\\FIXMLCodeGenerator\\mFinoFixml.xml";
            string EncrytFile = "A:\\MFS_v2_5\\Core\\settings\\developer\\mfino_encrypt_table_info.xml";
            DbType = "mysql";
			Language CodeFor = Language.Java;
			if (args.Length > 5)
			{
                DbType = args[5];
                EncrytFile = args[4];
				Dir = args[3];
				Prefix = args[2];
				FixmlFile = args[1];
				CodeFor = (Language)Enum.Parse(typeof(Language), args[0], true);
			}
			else if (args.Length > 4)
			{
                DbType = args[4];
                EncrytFile = args[3];
                Prefix = args[2];
				FixmlFile = args[1];
				CodeFor = (Language)Enum.Parse(typeof(Language), args[0], true);
			}
			else if (args.Length > 2)
			{
                EncrytFile = args[2];
				FixmlFile = args[1];
				CodeFor = (Language)Enum.Parse(typeof(Language), args[0], true);
			}
			else if (args.Length > 1)
			{
                EncrytFile = args[1];
				CodeFor = (Language)Enum.Parse(typeof(Language), args[0], true);
			};
			XmlDocument Doc = new XmlDocument();
			Doc.Load(FixmlFile);
			FixTop = Doc.FirstChild;

            XmlDocument EncryptDoc = new XmlDocument();
            EncryptDoc.Load(EncrytFile);
            EncryptTop = EncryptDoc.FirstChild;

            DbNode = FixTop.SelectSingleNode("/fix/primitives/dbtype[@name='" + DbType + "']");
            //DbNode = FixTop.SelectSingleNode("/fix/primitives");
			string BaseFileName;
			BaseFileName = Dir + "/" + Prefix;

			DefaultBeginString = Prefix + "." + FixTop.Attributes.GetNamedItem("major").Value + "." + FixTop.Attributes.GetNamedItem("minor").Value;

			if (CodeFor == Language.CPP)
			{
				CPPFile = new StreamWriter(BaseFileName + ".cpp");
				HFile = new StreamWriter(BaseFileName + ".h");
				HFile.WriteLine("#include <map>");
				Prefix += "_";
			}
			if (CodeFor == Language.Java)
			{
				JavaDir = Dir;
				JavaPrefix = Prefix;
				BaseFileName = Dir + "/C" + Prefix;
				JavaFile = new StreamWriter(BaseFileName + ".java");
				JavaFile.WriteLine("package com.mfino.fix;");
                JavaFile.WriteLine("import com.mfino.hibernate.Timestamp;"); 
                JavaFile.WriteLine("import java.util.Date;");
				JavaFile.WriteLine("import java.util.HashSet;");
				JavaFile.WriteLine("import java.util.Set;");
                JavaFile.WriteLine("import java.math.BigDecimal;");
				JavaFile.WriteLine("import com.mfino.domain.*;");
                JavaFile.WriteLine("import org.slf4j.Logger;");
                JavaFile.WriteLine("import org.slf4j.LoggerFactory;");
                JavaFile.WriteLine("import java.sql.Blob;");

				JavaFile.WriteLine("public abstract class C" + Prefix + "{");
				Prefix = "";
                JavaFile.WriteLine("private static Logger log = LoggerFactory.getLogger(CmFinoFIX.class);");
			}
			if (CodeFor == Language.JavaScript)
			{
				JSFile = new StreamWriter(BaseFileName + ".js");
				//write jslint option instruction
				JSFile.WriteLine("/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */");
				JSFile.WriteLine("/*global Ext: true */");

				JSFile.WriteLine("C" + Prefix + "={version: '" + FixTop.Attributes.GetNamedItem("major").Value + "." + FixTop.Attributes.GetNamedItem("minor").Value + "'};");
				CurrentJSScope = "C" + Prefix;
				CurrentJSShortScope = "C" + Prefix;
				TopMostJSScope = "C" + Prefix;
			}
			if (CodeFor == Language.Sql)
			{
				String outputFilePath = Path.Combine(Dir, "StaticData.sql");
				SqlStaticDataGenerator.Generate(FixTop, outputFilePath);
			}


			/*
			DefinesTable.Add("STRING", "String");
			DefinesTable.Add("MULTIPLEVALUESTRING", "String");
			DefinesTable.Add("COUNTRY", "String");
			DefinesTable.Add("PRICE", "double");
			DefinesTable.Add("INT", "int32_t");
			DefinesTable.Add("INT64", "int64_t");
			DefinesTable.Add("INT32", "int32_t");
			DefinesTable.Add("SEQNUM", "int32_t");
			DefinesTable.Add("LENGTH", "int32_t");
			DefinesTable.Add("NUMINGROUP", "int32_t");
			DefinesTable.Add("AMT", "double");
			DefinesTable.Add("QTY", "double");
			DefinesTable.Add("CURRENCY", "String");
			DefinesTable.Add("EXCHANGE", "String");
			DefinesTable.Add("LOCALMKTDATE", "FIX::CLocalMarketDate");
			DefinesTable.Add("CHAR", "char");
			DefinesTable.Add("UTCTIMESTAMP", "FIX::CUTCTimeStamp");
			DefinesTable.Add("BASE64", "FIX::CFIXBinary");
			DefinesTable.Add("FLOAT", "double");
			DefinesTable.Add("PRICEOFFSET", "double");
			DefinesTable.Add("PERCENTAGE", "double");
			DefinesTable.Add("MONTHYEAR", "FIX::CMonthYear");
			DefinesTable.Add("DAYOFMONTH", "int32_t");
			DefinesTable.Add("UTCDATE", "FIX::CUTCDate");
			DefinesTable.Add("UTCTIMEONLY", "FIX::CUTCTime");
			*/

			/*Java*/
			DefinesTable.Add("STRING", "String");
			/*Java*/
			DefinesTable.Add("BLOB", "String");
			/*Java*/
			DefinesTable.Add("MULTIPLEVALUESTRING", "String");
			/*Java*/
			DefinesTable.Add("COUNTRY", "String");
			/*Java*/
			DefinesTable.Add("PRICE", "Double");
			/*Java*/
			DefinesTable.Add("INT", "Integer");
			/*Java*/
			DefinesTable.Add("INT64", "Long");
			/*Java*/
			DefinesTable.Add("INT32", "Integer");
			/*Java*/
			DefinesTable.Add("SEQNUM", "Integer");
			/*Java*/
			DefinesTable.Add("LENGTH", "Integer");
			/*Java*/
			DefinesTable.Add("NUMINGROUP", "Integer");
			/*Java*/
			DefinesTable.Add("AMT", "Double");
			/*Java*/
			DefinesTable.Add("QTY", "Double");
			/*Java*/
			DefinesTable.Add("CURRENCY", "String");
			/*Java*/
			DefinesTable.Add("EXCHANGE", "String");
			/*Java*/
			DefinesTable.Add("LOCALMKTDATE", "CUTCDate");
			/*Java*/
			DefinesTable.Add("CHAR", "Character");
			/*Java*/
			DefinesTable.Add("UTCTIMESTAMP", "Timestamp");
			/*Java*/
			DefinesTable.Add("BASE64", "CFIXBinary");
			/*Java*/
			DefinesTable.Add("FLOAT", "Double");
			/*Java*/
			DefinesTable.Add("PRICEOFFSET", "Double");
			/*Java*/
			DefinesTable.Add("PERCENTAGE", "Double");
			/*Java*/
			DefinesTable.Add("MONTHYEAR", "CMonthYear");
			/*Java*/
			DefinesTable.Add("DAYOFMONTH", "Integer");
			/*Java*/
			DefinesTable.Add("UTCDATE", "CUTCDate");
			/*Java*/
			DefinesTable.Add("UTCTIMEONLY", "CUTCTime");
			/*Java*/
			DefinesTable.Add("BOOLEAN", "Boolean");
            /*Java*/
            DefinesTable.Add("NEWAMT", "BigDecimal");
            /*Java*/
            DefinesTable.Add("BINARYBLOB","Blob");
            /*Java*/
            DefinesTable.Add("BYTEARRAY","byte[]");

			XmlNodeList Fields = FixTop.SelectNodes("/fix/fields/field");

			DumpFieldNumbers(Prefix, Fields, CodeFor);
			SetMessageTypes(Prefix, FixTop.SelectNodes("/fix/messages/message"), CodeFor);
			SetFieldDefs(Prefix, Fields, CodeFor);

			HeaderNode = FixTop.SelectSingleNode("/fix/header");
			DumpClass(Prefix, HeaderNode, null, CodeFor,false);
			TrailerNode = FixTop.SelectSingleNode("/fix/trailer");
			DumpClass(Prefix, TrailerNode, null, CodeFor,false);

			if (CodeFor == Language.JavaScript)
			{
				JSFile.WriteLine(CurrentJSScope + ".record={};");
				JSFile.WriteLine(CurrentJSScope + ".list={};");
				JSFile.WriteLine(CurrentJSScope + ".message={};");
				DumpJSImporter(Prefix, CodeFor);
				DumpJSExporter(Prefix, CodeFor);
			}

			foreach (XmlNode Node in FixTop.SelectNodes("/fix/classes/class"))
			{
				MergeComponents(Node);

				if (Node.Attributes.GetNamedItem(CodeFor.ToString()) != null
						&& Node.Attributes.GetNamedItem(CodeFor.ToString()).Value == "Y")
				{
					DumpClass(Prefix, Node, null, CodeFor,false);
				}
			}

			foreach (XmlNode Node in FixTop.SelectNodes("/fix/records/record"))
			{
				MergeComponents(Node);


                if (Node.Attributes.GetNamedItem(CodeFor.ToString()) != null
						&& Node.Attributes.GetNamedItem(CodeFor.ToString()).Value == "Y")
				{
					DumpClass(Prefix, Node, null, CodeFor,false);
				}
			}
			foreach (XmlNode Node in FixTop.SelectNodes("/fix/records/list"))
			{
				MergeComponents(Node);
				if (Node.Attributes.GetNamedItem(CodeFor.ToString()) != null
						&& Node.Attributes.GetNamedItem(CodeFor.ToString()).Value == "Y")
				{
					DumpClass(Prefix, Node, null, CodeFor,false);
				}
				Node.ParentNode.RemoveChild(Node);
			}

			foreach (XmlNode Node in FixTop.SelectNodes("/fix/messages/message"))
			{
				MergeComponents(Node);

				if (Node.Attributes.GetNamedItem(CodeFor.ToString()) != null
						&& Node.Attributes.GetNamedItem(CodeFor.ToString()).Value == "Y")
				{
					DumpClass(Prefix, Node, null, CodeFor,false);
				}
			}

			DumpDerivedMessagesConstructor(Prefix, CodeFor);

			if (CodeFor == Language.CPP)
			{
				DumpEnumsArray(Prefix);
				DumpISO8583Elements(Prefix, Language.CPP);
				if (SQLRecords != "")
				{
					HFile.WriteLine("EXPORTABLE_FIX_API\tCFIXRecord\t**GetSQLTablesToCreate();");
					SQLRecords += "\nNULL\n};";
					CPPFile.WriteLine(SQLRecords);
					CPPFile.WriteLine("CFIXRecord\t**GetSQLTablesToCreate(){return\tg_SQLTablesToCreate;}");
				}
				HFile.Close();
				CPPFile.Close();
			}
			if (CodeFor == Language.Java)
			{
				JavaFile.WriteLine("}");
				JavaFile.Close();
			}
			if (CodeFor == Language.JavaScript)
			{
				JSFile.WriteLine(AllJSFieldsNames);
				JSFile.Close();
			}

			try
			{
				while (RemoveComponentElements(FixTop) > 0) ;

				Doc.Save(FixmlFile.Replace(".xml", "_Generated.xml"));
			}
			catch
			{
			}
		}
	}
}






