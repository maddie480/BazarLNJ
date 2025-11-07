#!/bin/bash

set -xeo pipefail

cd `dirname "$0"`
origdir=`pwd`
mkdir -p /tmp/gp-explorer
cd /tmp/gp-explorer

curl --fail -o commons-io.jar https://repo1.maven.org/maven2/commons-io/commons-io/2.21.0/commons-io-2.21.0.jar

curl -L --retry 3 --retry-all-errors -o gpsfiles-001.tar https://files.gamebanana.com/bitpit/gpsfiles-001.tar
curl -L --retry 3 --retry-all-errors -o gpsfiles-002.tar https://files.gamebanana.com/bitpit/gpsfiles-002.tar
curl -L --retry 3 --retry-all-errors -o gpsfiles-003.tar https://files.gamebanana.com/bitpit/gpsfiles-003.tar
cat gpsfiles-* > gpsfiles.tar
tar xvf gpsfiles.tar
rm *.tar

for file in source/*; do
    java -classpath "$origdir/out/production/TextureExtractor-src:commons-io.jar" ovh.maddie480.GPExplorer "$file" "target/`basename "$file"`_contents"
done

rm commons-io.jar