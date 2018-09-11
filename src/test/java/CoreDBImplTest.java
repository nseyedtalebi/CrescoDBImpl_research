import io.cresco.library.db.AgentRecord;
import io.cresco.library.db.PluginRecord;
import io.cresco.library.db.RegionRecord;
import jpastuff.PersistenceUnitInfoBuilder;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

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
    protected Map propertyMap = new HashMap<>();

    /*Init mocks*/
    //@Mock
    //protected CoreDBImpl testDb;
    @Mock
    protected PersistenceProvider provider;
    @Mock
    protected EntityManagerFactory entityManagerFactory;
    @Mock
    protected EntityManager entityManager;
    @Mock
    protected RegionRecord region1;
    @Mock
    protected RegionRecord region2;
    @Mock
    protected AgentRecord agent1;
    @Mock
    protected AgentRecord agent2;
    @Mock
    protected PluginRecord plugin1;
    @Mock
    protected PluginRecord plugin2;

    protected final PersistenceUnitInfo puInfo = new PersistenceUnitInfoBuilder("test_pu","org.datanucleus.api.jpa.PersistenceProviderImpl").build();
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
        Query mockedQuery = mock(Query.class);
        when(mockedQuery.getResultStream()).thenReturn(Stream.of(region1,region2));
        when(testDb.entityManager.createQuery(CoreDBImpl.REGION_QUERY_STRING)).thenReturn(mockedQuery);
        Set<RegionRecord> regionsReturned = testDb.getRegions().collect(Collectors.toSet());
        //remember: set A = set B iff A is subset of B and B is subset of A
        assertTrue(regionsReturned.containsAll(expectedRegions) && expectedRegions.containsAll(regionsReturned));
    }




 }
