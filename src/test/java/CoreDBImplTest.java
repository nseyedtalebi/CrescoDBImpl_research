import io.cresco.library.utilities.CLogger;
import jpastuff.PersistenceUnitInfoBuilder;
import net.bytebuddy.implementation.bytecode.Throw;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
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
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tags({@Tag("unit")})
public class CoreDBImplTest {
    protected PersistenceUnitInfo puInfo;

    @BeforeEach
    public void setPuInfo(){
        this.puInfo = new PersistenceUnitInfoBuilder("test_pu","org.datanucleus.api.jpa.PersistenceProviderImpl").build();
    }


    public Stream<Arguments> getProviderFromClassNameTest_genTestParams(){
        return Stream.of(
                Arguments.of("does.not.exist",ReflectiveOperationException.class,"Class not found")
                ,Arguments.of("javax.persistence.spi.PersistenceProvider",ReflectiveOperationException.class, "Instantiation exception")
                ,Arguments.of("DummyClasses.PrivateConstructor",ReflectiveOperationException.class,"Illegal Access exception")
                ,Arguments.of("org.datanucleus.api.jpa.PersistenceProviderImpl",null,"No exception")
                ,Arguments.of("DummyClasses.WrongConstructor",ReflectiveOperationException.class,"No such method exception")
                ,Arguments.of("DummyClasses.BadNullConstructor",ReflectiveOperationException.class,"Constructor throws exception")
        );
    }

    @ParameterizedTest
    @MethodSource("getProviderFromClassNameTest_genTestParams")
    public void getProviderFromClassNameTest(String providerClassName, Class<Throwable> underlyingException, String testDesc){
        Executable getProvider  = ()-> CoreDBImpl.getProviderFromClassName(providerClassName);
        if(underlyingException != null){
            assertThrows(ReflectiveOperationException.class,getProvider);
        } else assertDoesNotThrow(getProvider);
    }



 }
