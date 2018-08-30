package jpastuff;
import org.junit.jupiter.api.*;
import org.mockito.Mock;

import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tags({@Tag("jpastuff"),@Tag("unit")})
public class PersistenceUnitInfoImplTest {

    /*private static DynamicTest createBuilderMethodTest(Method method){
        String displayName = "Check "+method.getName();
        return DynamicTest.dynamicTest(displayName,()->method.)
    }
    @TestFactory
    public Stream<DynamicTest> testBuilderMethods() {
    List methodNames = Arrays.asList(new String[]{"PersistenceUnitName","PersistenceProviderClassName"
            ,"PersistenceUnitTransactionType","JtaDataSource"
            ,"NonJtaDataSource","MappingFileNames","ManagedClassNames"
            ,"SharedCacheMode","ValidationMode","Properties"
            ,"GetJarFileUrls"
        });
        Stream<Method> methods = Arrays.asList(PersistenceUnitInfoBuilder.class.getMethods()).stream();
        methods.filter( (method)->methodNames.contains("set"+method.getName()))
                .map( (method) -> DynamicTest.dynamicTest("Check "+method.getName()));
    }
    */

    /*Writing lot of boilerplate code by hand irritates the piss out of me so I
    spent that time writing a snippet of Python to handle the necessary but very
    boring stuff.
    methods = ("PersistenceUnitName","PersistenceProviderClassName"
               ,"PersistenceUnitTransactionType","JtaDataSource"
               ,"NonJtaDataSource","MappingFileNames","ManagedClassNames"
               ,"SharedCacheMode","ValidationMode","Properties"
               ,"jarFileUrls")
    testCases = ["@Test\npublic void set{method}_test(|args|){{ \n\
    PersistenceUnitInfoBuilder mybuilder = new PersistenceUnitInfoBuilder();\n\
    mybuilder.set{method}(|some args|);\n\
    PersistenceUnitInfoImpl myimpl = mybuilder.build();\n\
    assertEquals(|some args|,myimpl.get{method}());}}\n".format(method=method) for\
    method in methods]
    for case in testCases:
         print(case)

     */
    @Test
    public void setPersistenceUnitName_test(){
        PersistenceUnitInfoBuilder mybuilder = new PersistenceUnitInfoBuilder();
        mybuilder.setPersistenceUnitName("test");
        PersistenceUnitInfoImpl myimpl = mybuilder.build();
        assertEquals("test",myimpl.getPersistenceUnitName());
    }

    @Test
    public void setPersistenceProviderClassName_test(){
        PersistenceUnitInfoBuilder mybuilder = new PersistenceUnitInfoBuilder();
        mybuilder.setPersistenceProviderClassName("test.class.name");
        PersistenceUnitInfoImpl myimpl = mybuilder.build();
        assertEquals("test.class.name",myimpl.getPersistenceProviderClassName());
    }

    @Test
    public void setPersistenceUnitTransactionType_test(){
        PersistenceUnitInfoBuilder mybuilder = new PersistenceUnitInfoBuilder();
        mybuilder.setPersistenceUnitTransactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL);
        PersistenceUnitInfoImpl myimpl = mybuilder.build();
        assertEquals(PersistenceUnitTransactionType.RESOURCE_LOCAL,myimpl.getTransactionType());
    }

    @Test
    public void setJtaDataSource_test(){
        PersistenceUnitInfoBuilder mybuilder = new PersistenceUnitInfoBuilder();
        DataSource mockds = mock(DataSource.class);
        mybuilder.setJtaDataSource(mockds);
        PersistenceUnitInfoImpl myimpl = mybuilder.build();
        assertEquals(mockds,myimpl.getJtaDataSource());
    }

    @Test
    public void setNonJtaDataSource_test(){
        PersistenceUnitInfoBuilder mybuilder = new PersistenceUnitInfoBuilder();
        DataSource nonjta = mock(DataSource.class);
        mybuilder.setNonJtaDataSource(nonjta);
        PersistenceUnitInfoImpl myimpl = mybuilder.build();
        assertEquals(nonjta,myimpl.getNonJtaDataSource());
    }

    @Test
    public void setMappingFileNames_test(){
        PersistenceUnitInfoBuilder mybuilder = new PersistenceUnitInfoBuilder();
        List mappingFileNames = mock(List.class);
        mybuilder.setMappingFileNames(mappingFileNames);
        PersistenceUnitInfoImpl myimpl = mybuilder.build();
        assertEquals(mappingFileNames,myimpl.getMappingFileNames());
    }

    @Test
    public void setManagedClassNames_test(){
        PersistenceUnitInfoBuilder mybuilder = new PersistenceUnitInfoBuilder();
        List managedClassNames = mock(List.class);
        mybuilder.setManagedClassNames(managedClassNames);
        PersistenceUnitInfoImpl myimpl = mybuilder.build();
        assertEquals(managedClassNames,myimpl.getManagedClassNames());
    }

    @Test
    public void setSharedCacheMode_test(){
        PersistenceUnitInfoBuilder mybuilder = new PersistenceUnitInfoBuilder();
        SharedCacheMode m = SharedCacheMode.UNSPECIFIED;
        mybuilder.setSharedCacheMode(m);
        PersistenceUnitInfoImpl myimpl = mybuilder.build();
        assertEquals(m,myimpl.getSharedCacheMode());
    }

    @Test
    public void setValidationMode_test(){
        PersistenceUnitInfoBuilder mybuilder = new PersistenceUnitInfoBuilder();
        ValidationMode m = ValidationMode.AUTO;
        mybuilder.setValidationMode(m);
        PersistenceUnitInfoImpl myimpl = mybuilder.build();
        assertEquals(m,myimpl.getValidationMode());
    }

    @Test
    public void setProperties_test(){
        PersistenceUnitInfoBuilder mybuilder = new PersistenceUnitInfoBuilder();
        Properties p = mock(Properties.class);
        mybuilder.setProperties(p);
        PersistenceUnitInfoImpl myimpl = mybuilder.build();
        assertEquals(p,myimpl.getProperties());
    }

    @Test
    public void setJarFileUrls_test(){
        PersistenceUnitInfoBuilder mybuilder = new PersistenceUnitInfoBuilder();
        List l = mock(List.class);
        mybuilder.setJarFileUrls(l);
        PersistenceUnitInfoImpl myimpl = mybuilder.build();
        assertEquals(l,myimpl.getJarFileUrls());
    }

    @Test
    public void build_checkForRequired(){
        PersistenceUnitInfoBuilder mybuilder = new PersistenceUnitInfoBuilder();
        DataSource mockds = mock(DataSource.class);
        mybuilder.setPersistenceUnitName("test")
                 .setPersistenceProviderClassName("test.class")
                 .setNonJtaDataSource(mockds);
        assertDoesNotThrow(mybuilder::build);
    }

    //TODO:Figure out minimal set of options for persistence context info
    //then modify build() method to enforce this minimum.
    @Test
    public void build_checkForRequired_notEnoughArgs(){
        PersistenceUnitInfoBuilder mybuilder = new PersistenceUnitInfoBuilder();
        DataSource mockds = mock(DataSource.class);
        mybuilder.setPersistenceUnitName("test")
                .setPersistenceProviderClassName("test.class");
        assertThrows(PersistenceUnitInfoBuilder.NotEnoughOptionsConfiguredException.class
        ,mybuilder::build);
    }

}
