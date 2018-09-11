import io.cresco.library.db.AgentRecord;
import io.cresco.library.db.PluginRecord;
import io.cresco.library.db.RegionRecord;
import jpastuff.PersistenceUnitInfoBuilder;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;

import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tags({@Tag("unit")})
public class CoreDBImplTest {
    //protected PersistenceUnitInfo puInfo;
    private Map propertyMap = new HashMap<>();

    /*Init mocks*/
    //@Mock
    //protected CoreDBImpl testDb;
    @Mock
    private PersistenceProvider provider;
    @Mock
    private EntityManagerFactory entityManagerFactory;
    @Mock
    private EntityManager entityManager;
    @Mock
    private RegionRecord region1;
    @Mock
    private RegionRecord region2;
    @Mock
    private AgentRecord agent1;
    @Mock
    private AgentRecord agent2;
    @Mock
    private PluginRecord plugin1;
    @Mock
    private PluginRecord plugin2;
    @Mock
    private Query mockedQuery;

    private final PersistenceUnitInfo puInfo = new PersistenceUnitInfoBuilder("test_pu","org.datanucleus.api.jpa.PersistenceProviderImpl").build();
    @InjectMocks
    protected CoreDBImpl testDb = new CoreDBImpl(puInfo);

    @BeforeAll
    public void beforeAlLTests(){
        this.propertyMap.put("key","value");
    }
    @BeforeEach
    public void beforeEachTest() throws ReflectiveOperationException {
        initMocks(this);

       /* when(entityManagerFactory.createEntityManager(anyMap())).thenReturn(entityManager);
        when(provider.createContainerEntityManagerFactory(any(PersistenceUnitInfo.class),anyMap())).thenReturn(entityManagerFactory);
        when(testDb.getProviderFromPUInfo()).thenReturn(provider);*/
    }


    private Stream<Arguments> getArgs_getProviderFromClassNameTest(){
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
    @MethodSource("getArgs_getProviderFromClassNameTest")
    public void getProviderFromPUInfoTest(String providerClassName, Class<Throwable> underlyingException, String testDesc){
        Executable getProvider  = ()-> {
            CoreDBImpl d = new CoreDBImpl(new PersistenceUnitInfoBuilder("test_pu",providerClassName).build());
            d.getProviderFromPUInfo();
        };
        if(underlyingException != null){
            assertThrows(underlyingException,getProvider);
        } else assertDoesNotThrow(getProvider);
    }


    @Test
    public void getRegionsTest(){
        Set<RegionRecord> expectedRegions = Stream.of(region1,region2).collect(Collectors.toSet());
        when(mockedQuery.getResultStream()).thenReturn(Stream.of(region1,region2));
        when(testDb.entityManager.createQuery(CoreDBImpl.REGION_QUERY_STRING)).thenReturn(mockedQuery);
        Set<RegionRecord> regionsReturned = testDb.getRegions().collect(Collectors.toSet());
        //remember: set A = set B iff A is subset of B and B is subset of A
        assertTrue(regionsReturned.containsAll(expectedRegions) && expectedRegions.containsAll(regionsReturned));
    }

    @Test
    public void getAgentsTest(){
        Set<AgentRecord> expectedAgents = Stream.of(agent1,agent2).collect(Collectors.toSet());
        when(mockedQuery.getResultStream()).thenReturn(Stream.of(agent1,agent2));
        when(testDb.entityManager.createQuery(CoreDBImpl.AGENT_QUERY_STRING)).thenReturn(mockedQuery);
        Set<AgentRecord> agentsReturned = testDb.getAgents().collect(Collectors.toSet());
        assertTrue(agentsReturned.containsAll(expectedAgents) && expectedAgents.containsAll(agentsReturned));
    }

    @Test
    public void getPluginsTest(){
        Set<PluginRecord> expectedPlugins = Stream.of(plugin1,plugin2).collect(Collectors.toSet());
        when(mockedQuery.getResultStream()).thenReturn(Stream.of(plugin1,plugin2));
        when(testDb.entityManager.createQuery(CoreDBImpl.PLUGIN_QUERY_STRING)).thenReturn(mockedQuery);
        Set<PluginRecord> pluginsReturned = testDb.getPlugins().collect(Collectors.toSet());
        assertTrue(pluginsReturned.containsAll(expectedPlugins) && expectedPlugins.containsAll(pluginsReturned));
    }

    /*Less redundant but more complex version of the three methods above
    public Stream<Arguments> supplyArgsToGetEntityTest() {
         return Stream.of(
                 Arguments.of("getRegions",CoreDBImpl.REGION_QUERY_STRING,Stream.of(region1,region2),RegionRecord.class),
                 Arguments.of("getAgents",CoreDBImpl.AGENT_QUERY_STRING,Stream.of(agent1,agent2),AgentRecord.class),
                 Arguments.of("getPlugins",CoreDBImpl.PLUGIN_QUERY_STRING,Stream.of(plugin1,plugin2),PluginRecord.class)
         );
    }
    @ParameterizedTest
    @MethodSource("supplyArgsToGetEntityTest")
    public void getEntityTest(String methodToCall, String queryString, Stream expectedEntities, Class elementReturnType)
    throws ReflectiveOperationException {
        Set expectedEntitySet = new HashSet();
        expectedEntities.forEach(expectedEntitySet::add);

        when(mockedQuery.getResultStream()).thenReturn(expectedEntitySet.stream());
        when(testDb.entityManager.createQuery(queryString)).thenReturn(mockedQuery);
        Stream<?> res = (Stream) testDb.getClass().getMethod(methodToCall,null).invoke(testDb,null);
        Set actualEntitiesReturned = new HashSet();
        res.forEach(actualEntitiesReturned::add);

        assertTrue(actualEntitiesReturned.containsAll(expectedEntitySet) && expectedEntitySet.containsAll(actualEntitiesReturned));
    }
    */

    private Stream<Arguments> getArgs_getAgentsInRegionTest(){
        return Stream.of(
                Arguments.of("region1",Stream.of(agent1)),
                Arguments.of("region2",Stream.of(agent2))
        );
    }
    @ParameterizedTest
    @MethodSource("getArgs_getAgentsInRegionTest")
    public void getAgentsInRegionTest(String regionName, Stream<AgentRecord> expectedAgents) {

    }





 }
