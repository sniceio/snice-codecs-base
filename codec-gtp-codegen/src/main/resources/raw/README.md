All these files are raw dumps from the various 3gpp specifications. These TSV files are then converted into yml files and put in the "specifications" subfolder, from which the GTP codegen tools load them, maps to them to POJOs and from there generates the actual code. The worklow is:

1. Get the spec, e.g. TS 29.060 and then copy/paste the GTPv1 Information Elements table into a tool that can spit out that table in a TSV format (I use LibreOffice Calc)
2. Once in a TSV format, the convert it into a yml file. There is a simple python script under the scripts folder. You will have to edit it.
3. Put the yml file in the "specifications" folder.
4. Create a POJO and then it is easy to load that yaml and map it to these POJOs (using jackson)

