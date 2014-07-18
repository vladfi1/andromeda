Once after cloning the repo you need to generated some files:

  ant -buildfile buildParser.xml
  ant -buildfile buildEnricher.xml

Thereafter you only need to:

  ant -buildfile release.xml
