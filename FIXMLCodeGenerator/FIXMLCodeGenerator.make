

# Warning: This is an automatically generated file, do not edit!

srcdir=.
top_srcdir=.

include $(top_srcdir)/config.make

ifeq ($(CONFIG),DEBUG)
ASSEMBLY_COMPILER_COMMAND = gmcs
ASSEMBLY_COMPILER_FLAGS =  -noconfig -codepage:utf8 -warn:4 -optimize- -debug "-define:DEBUG;TRACE"
ASSEMBLY = ../Debug/FIXMLCodeGenerator.exe
ASSEMBLY_MDB = $(ASSEMBLY).mdb
COMPILE_TARGET = exe
PROJECT_REFERENCES = 
BUILD_DIR = ../Debug/

MFINOFIXML_XML_SOURCE=mFinoFixml.xml
FIXMLCODEGENERATOR_EXE_MDB_SOURCE=../Debug/FIXMLCodeGenerator.exe.mdb
FIXMLCODEGENERATOR_EXE_MDB=$(BUILD_DIR)/FIXMLCodeGenerator.exe.mdb

endif

ifeq ($(CONFIG),RELEASE)
ASSEMBLY_COMPILER_COMMAND = gmcs
ASSEMBLY_COMPILER_FLAGS =  -noconfig -codepage:utf8 -warn:4 -optimize+ "-define:TRACE"
ASSEMBLY = ../Release/FIXMLCodeGenerator.exe
ASSEMBLY_MDB = 
COMPILE_TARGET = exe
PROJECT_REFERENCES = 
BUILD_DIR = ../Release/

MFINOFIXML_XML_SOURCE=mFinoFixml.xml
FIXMLCODEGENERATOR_EXE_MDB=

endif

AL=al2
SATELLITE_ASSEMBLY_NAME=$(notdir $(basename $(ASSEMBLY))).resources.dll

PROGRAMFILES = \
	$(MFINOFIXML_XML) \
	$(FIXMLCODEGENERATOR_EXE_MDB)  

BINARIES = \
	$(FIXMLCODEGENERATOR)  


RESGEN=resgen2

MFINOFIXML_XML = $(BUILD_DIR)/mFinoFixml.xml
FIXMLCODEGENERATOR = $(BUILD_DIR)/fixmlcodegenerator

FILES = \
	CodeGenerator.cs \
	Properties/AssemblyInfo.cs \
	SqlStaticDataGenerator.cs 

DATA_FILES = 

RESOURCES = 

EXTRAS = \
	fixmlcodegenerator.in 

REFERENCES =  \
	System \
	System.Core \
	System.Xml.Linq \
	System.Data.DataSetExtensions \
	System.Data \
	System.Xml

DLL_REFERENCES = 

CLEANFILES = $(PROGRAMFILES) $(BINARIES) 

#Targets
all-local: $(ASSEMBLY) $(PROGRAMFILES) $(BINARIES)  $(top_srcdir)/config.make



$(eval $(call emit-deploy-target,MFINOFIXML_XML))
$(eval $(call emit-deploy-wrapper,FIXMLCODEGENERATOR,fixmlcodegenerator,x))


$(eval $(call emit_resgen_targets))
$(build_xamlg_list): %.xaml.g.cs: %.xaml
	xamlg '$<'


$(ASSEMBLY) $(ASSEMBLY_MDB): $(build_sources) $(build_resources) $(build_datafiles) $(DLL_REFERENCES) $(PROJECT_REFERENCES) $(build_xamlg_list) $(build_satellite_assembly_list)
	make pre-all-local-hook prefix=$(prefix)
	mkdir -p $(shell dirname $(ASSEMBLY))
	make $(CONFIG)_BeforeBuild
	$(ASSEMBLY_COMPILER_COMMAND) $(ASSEMBLY_COMPILER_FLAGS) -out:$(ASSEMBLY) -target:$(COMPILE_TARGET) $(build_sources_embed) $(build_resources_embed) $(build_references_ref)
	make $(CONFIG)_AfterBuild
	make post-all-local-hook prefix=$(prefix)

install-local: $(ASSEMBLY) $(ASSEMBLY_MDB)
	make pre-install-local-hook prefix=$(prefix)
	make install-satellite-assemblies prefix=$(prefix)
	mkdir -p '$(DESTDIR)$(libdir)/$(PACKAGE)'
	$(call cp,$(ASSEMBLY),$(DESTDIR)$(libdir)/$(PACKAGE))
	$(call cp,$(ASSEMBLY_MDB),$(DESTDIR)$(libdir)/$(PACKAGE))
	$(call cp,$(MFINOFIXML_XML),$(DESTDIR)$(libdir)/$(PACKAGE))
	$(call cp,$(FIXMLCODEGENERATOR_EXE_MDB),$(DESTDIR)$(libdir)/$(PACKAGE))
	mkdir -p '$(DESTDIR)$(bindir)'
	$(call cp,$(FIXMLCODEGENERATOR),$(DESTDIR)$(bindir))
	make post-install-local-hook prefix=$(prefix)

uninstall-local: $(ASSEMBLY) $(ASSEMBLY_MDB)
	make pre-uninstall-local-hook prefix=$(prefix)
	make uninstall-satellite-assemblies prefix=$(prefix)
	$(call rm,$(ASSEMBLY),$(DESTDIR)$(libdir)/$(PACKAGE))
	$(call rm,$(ASSEMBLY_MDB),$(DESTDIR)$(libdir)/$(PACKAGE))
	$(call rm,$(MFINOFIXML_XML),$(DESTDIR)$(libdir)/$(PACKAGE))
	$(call rm,$(FIXMLCODEGENERATOR_EXE_MDB),$(DESTDIR)$(libdir)/$(PACKAGE))
	$(call rm,$(FIXMLCODEGENERATOR),$(DESTDIR)$(bindir))
	make post-uninstall-local-hook prefix=$(prefix)
