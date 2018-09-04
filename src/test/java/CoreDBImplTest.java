import jpastuff.PersistenceUnitInfoBuilder;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;
import java.io.File;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.ProviderUtil;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tags({@Tag("unit")})
public class CoreDBImplTest {
    /*Eventually, these should be put into the main cresco controller with
     a JUnit 5 @Tag for integration testing. Same for other integration tests.
     That way they're part of the larger project without having to be run during
     every build of every piece. Travis can probably help with this so the integration
     tests are only run when they need to be
     */

    /*Finish unittest for constructors, then write integration tests in
    such a way that we can run the whole suite of them for different backend
    providers
     */
    /*@TestFactory
    Stream<DynamicTest> checkPersistenceXML() throws ParserConfigurationException, IOException, SAXException {
        ClassLoader classLoader = getClass().getClassLoader();
        //System.out.println(classLoader.getResource("persistence.xml"));
        File persistenceXmlFile = new File(classLoader.getResource("persistence.xml").getFile());
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(persistenceXmlFile);
        NodeList persistenceUnitNodes = document.getElementsByTagName("persistence-unit");
        return Stream.of(
                DynamicTest.dynamicTest("Check # of pesistence unit nodes",()-> assertEquals(persistenceUnitNodes.getLength(),1))
                ,DynamicTest.dynamicTest("Check default persistence node name",()->assertEquals(persistenceUnitNodes.item(0).getAttributes().getNamedItem("name").getNodeValue(),CoreDBImpl.DEFAULT_PERSISTENCE_UNIT_NAME))
        );
    }*/
    @Test
    public void constructor_throwsClassNotFound(){
        PersistenceUnitInfo puInfo = new PersistenceUnitInfoBuilder("test_pu","does.not.exist").build();
        assertThrows(ClassNotFoundException.class,()-> new CoreDBImpl(puInfo,null,null));
    }
    //TODO:Continue writing unit tests for constructor. Prob need one for each kind of exception thrown, plus something to verify every non-trivial thing.

}
