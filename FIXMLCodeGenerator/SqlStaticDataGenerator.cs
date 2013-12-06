using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;
using System.IO;

namespace FIXMLCodeGenerator
{
    public class SqlStaticDataGenerator
    {
        private static String sqlTemplate = "INSERT IGNORE INTO `mfino`.`enum_text` " + 
            "(VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, "
            + "EnumCode, EnumValue, DisplayText) VALUES ('{0}',{1},'{2}',{3},'{4}','{5}','{6}','{7}','{8}','{9}','{10}');";
	
        public static void Generate(XmlNode documentRoot, String outputFilePath)
        {
            using (StreamWriter output = new StreamWriter(outputFilePath))
            {
                output.WriteLine("DELETE FROM `mfino`.`enum_text`;");

                XmlNodeList fields = documentRoot.SelectNodes("./fields/field");

                foreach (XmlNode field in fields)
                {
                    XmlNode baseField = CodeGenerator.FindBaseField(field);

                    if (baseField.HasChildNodes)
                    {
                        XmlNodeList values = baseField.SelectNodes("./value");
                        foreach (XmlNode value in values)
                        {
                            string sqlInsertStatement = string.Format(sqlTemplate,
                                1, "NOW()","system",
                                "NOW()", "system", 
                                0,
                                field.Attributes["name"].Value,
                                field.Attributes["number"].Value,
                                value.Attributes["enum"].Value,
                                value.Attributes["description"].Value,
                                value.Attributes["description"].Value
                                );

                            output.WriteLine(sqlInsertStatement);
                        }
                    }
                }
            }
        }
    }
}
