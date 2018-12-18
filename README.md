## Installation & configuration

### Generate jars

```sh
mvn package -DskipTests
```

### Install jars

```sh
cp `find -name "*.jar"` /var/lib/nuxeo/server/nxserver/bundles/
cp `find ~/.m2 -name "java-jwt-3.2.0.jar"` /var/lib/nuxeo/server/nxserver/bundles/
```

### Configuration

Create file `/var/lib/nuxeo/server/nxserver/config/onlyoffice-config.xml` with

```xml
<?xml version="1.0"?>
<component name="fr.edu.lyon.onlyoffice.ConfigService.contrib">
  <extension target="fr.edu.lyon.onlyoffice.ConfigService" point="config">
    <onlyoffice>
      <url>https://onlyoffice.univ.fr/</url>
      <algorithm>onlyoffice</algorithm>
      <prefix>onlyo</prefix>
    </onlyoffice>
  </extension>
</component>
```

and create file `/var/lib/nuxeo/server/nxserver/config/onlyoffice-auth-config.xml` with

```xml
<component name="onlyoffice.auth.config">
  <require>fr.esupportail.nuxeo.onlyoffice.jwt.auth</require>
  <extension target="org.nuxeo.ecm.platform.ui.web.auth.service.PluggableAuthenticationService"
      point="specificChains">
        <specificAuthenticationChain name="onlyoffice">
                <urlPatterns>
                        <url>(.*)/site/onlyoffice/(download|template|callback).*</url>
                </urlPatterns>
                <replacementChain>
                        <plugin>ONLYOFFICE_JWT_AUTH</plugin>
                </replacementChain>
        </specificAuthenticationChain>
  </extension>
</component>
```

The algorithm and the prefix are used to generate the JWT (passed as parameter `sessionToken`)

Create file `/var/lib/nuxeo/server/nxserver/config/onlyoffice-jwt-config.xml` with

```xml
<?xml version="1.0"?>
<component name="fr.edu.lyon.jwt.service.PayloadPluginService.JwtSign">
  <extension target="fr.edu.lyon.jwt.service.PayloadPluginService" point="jwt-sign">
    <algorithm id="onlyoffice" name="HS256" key="secretkey" />
  </extension>
</component>
```

### Take changes into account

You have to restart nuxeo

## Sequence diagram

Diagrams generated with : `plantuml diagrammes.puml`

### Modify a document already existing in Nuxeo

![](docs/diagramme-edit.png)

## Explications du code

### nuxeo-onlyoffice-core/src/main/resources/OSGI-INF/extensions/actions-contrib.xml
- ajout boutons "Edition en ligne", "Co-édition en ligne" (ie "autosave"), "Prévisualisation", "Créer"
- avec des `link` construits par

### nuxeo-onlyoffice-core/src/main/java/fr/edu/lyon/nuxeo/onlyoffice/view/OnlyOfficeActionsBean.java
- contenant .../site/onlyoffice/(edit|coedit|view|create)/&lt;docId&gt;
- cette url est gérée par

### nuxeo-onlyoffice-core/src/main/java/fr/edu/lyon/nuxeo/onlyoffice/webengine/OnlyOfficeWebservice.java
- les actions correspondant aux boutons créent une page javascript avec le template
#### nuxeo-onlyoffice-core/src/main/resources/skin/views/OnlyOffice/index.ftl
- avec comme `config`
  - `url` : .../site/onlyoffice/(download|template)/&lt;docId&gt;?sessionToken=&lt;sessionToken&gt;
  - `callbackUrl` : .../site/onlyoffice/(callbackCreate|callbackEdit|callbackCoEdit)/&lt;docId&gt;
  - généré par nuxeo-onlyoffice-core/src/main/java/fr/edu/lyon/nuxeo/onlyoffice/service/OnlyofficeConfig.java
