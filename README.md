# UMLS-concept-assertion-mapper
1;4205;0c## Setting up the input as a MySQL database

## Running the pipeline
To run the main file (Test.java in src/main/java/edu/uw/bhi/bionlp/pipeline), change directory to *PipelineScripts* and then type:
```
java -Xmx4g -Dlog4j.configurationFile=file:////home/vpejaver/repos/UMLS-concept-assertion-mapper/PipelineScripts/src/main/resources/log4j2.xml -cp "src/main/java:src/main/resources:lib/*" edu.uw.bhi.bionlp.pipeline.Test <DB server> <DB name> <Table name> <Verbose> <Max heap size> <Start note ID> <End note ID> <Username> <Password>
```
where,
* DB server can be localhost or an IP address of the server hosting the input MySQL table
* DB name is the name of the MySQL database
* Table name is the name of the table containing input data
* Verbose is a Boolean (0 or 1) indicating the need for verbose outputs
* Max heap size is the maximum heap size for Java (determines memory usage), typically an integer followed by g, m or k
* Start note ID is the ID number of the first report to be processed
* End note ID is the ID number of the last report to be processed
* Username is the username for the MySQL database
* Password is the password for access to this database

Here is an example:
```
java -Xmx4g -Dlog4j.configurationFile=file:////home/vpejaver/repos/UMLS-concept-assertion-mapper/PipelineScripts/src/main/resources/log4j2.xml -cp "src/main/java:src/main/resources:lib/*" edu.uw.bhi.bionlp.pipeline.Test localhost CLEAR TAN_DATASET_SEG_OP 0 4g 1 871 vpejaver password
```