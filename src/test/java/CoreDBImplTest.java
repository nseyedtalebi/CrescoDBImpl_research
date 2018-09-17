import jpa.PersistenceUnitInfoBuilder;
import jpa.entities.AgentRecord;
import jpa.entities.PluginRecord;
import jpa.entities.RegionRecord;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import queryresults.AgentListResult;
import queryresults.RegionListResult;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tags({@Tag("unit")})
public class CoreDBImplTest {
    //protected PersistenceUnitInfo puInfo;
    private Map propertyMap = new HashMap<>();
    private final PersistenceUnitInfo puInfo = new PersistenceUnitInfoBuilder("test_pu","some.class").build();
    @Mock
    private PersistenceProvider provider;
    @Mock
    private EntityManagerFactory entityManagerFactory;
    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery mockedQuery;


    @InjectMocks
    protected CoreDBImpl testDb = new CoreDBImpl(puInfo);

    @BeforeAll
    public void beforeAlLTests(){
        this.propertyMap.put("key","value");
    }
    @BeforeEach
    public void beforeEachTest() {
        initMocks(this);
    }

    private Stream<Arguments> getArgs_getProviderFromClassNameTest(){
        return Stream.of(
                Arguments.of("does.not.exist",ClassNotFoundException.class,"Class not found")
                ,Arguments.of("DummyClasses.WrongConstructor",NoSuchMethodException.class,"No such method exception")
                ,Arguments.of("DummyClasses.BadNullConstructor",
                        InvocationTargetException.class,"Constructor throws exception")
        );
    }
    @ParameterizedTest(name = "Check for {2} when given {0}")
    @MethodSource("getArgs_getProviderFromClassNameTest")
    public void getProviderFromPUInfoTest(String providerClassName, Class<Throwable> underlyingException, String testDesc){
        CoreDBImpl d = new CoreDBImpl(new PersistenceUnitInfoBuilder("test_pu",providerClassName).build());
        try {
            d.getProviderFromPUInfo();
        } catch(ReflectiveOperationException ex){
            assertEquals(underlyingException,ex.getCause().getClass());
        }
    }

    public Stream<Arguments> supplyArgsToGetEntityTest() {
         return Stream.of(
                 Arguments.of("getRegions",CoreDBImpl.REGION_QUERY_STRING),
                 Arguments.of("getAgents",CoreDBImpl.AGENT_QUERY_STRING),
                 Arguments.of("getPlugins",CoreDBImpl.PLUGIN_QUERY_STRING),
                 Arguments.of("getRegionList",CoreDBImpl.QRY_DBINTERFACE_GETREGIONLIST),
                 Arguments.of("getAgentList",CoreDBImpl.QRY_DBINTERFACE_GETAGENTLIST)
         );
    }
    @ParameterizedTest
    @MethodSource("supplyArgsToGetEntityTest")
    public void getEntityTest(String methodToCall, String queryString, Class resultType)
    throws ReflectiveOperationException {
        when(mockedQuery.getResultStream()).thenReturn(Stream.empty());
        when(testDb.entityManager.createQuery(eq(queryString),any(Class.class))).thenReturn(mockedQuery);
        testDb.getClass().getMethod(methodToCall,null).invoke(testDb,null);
        verify(mockedQuery).getResultStream();
    }


    private Stream<Arguments> getArgs_getAgentsInRegionTest(){
        return Stream.of(
                Arguments.of("getAgentsInRegion",CoreDBImpl.QRY_AGENTS_IN_REGION,"rName"),
                Arguments.of("getPluginsInAgent",CoreDBImpl.QRY_PLUGINS_IN_AGENT,"aName")
        );
    }
    @ParameterizedTest
    @MethodSource("getArgs_getAgentsInRegionTest")
    public void getAgentsInRegionTest(
            String methodName, String queryString, String expectedQueryParam
    ) throws ReflectiveOperationException {
        when(mockedQuery.getResultStream()).thenReturn(Stream.empty());
        when(mockedQuery.setParameter(eq(expectedQueryParam), any())).thenReturn(mockedQuery);
        when(testDb.entityManager.createQuery(eq(queryString),any(Class.class))).thenReturn(mockedQuery);
        testDb.getClass().getMethod(methodName,String.class).invoke(testDb,"");
        verify(mockedQuery).getResultStream();
        verify(mockedQuery).setParameter(eq(expectedQueryParam), any());
    }



 }
