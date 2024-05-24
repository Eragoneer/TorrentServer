package bg.sofia.uni.fmi.mjt;

import bg.sofia.uni.fmi.mjt.repository.FileRepository;
import bg.sofia.uni.fmi.mjt.response.Response;
import bg.sofia.uni.fmi.mjt.response.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileRepositoryTest {
    private FileRepository repository;

    @BeforeEach
    void setUp(){
        repository = new FileRepository();
    }

    @Test
    public void testRegisterValidPaths(){
        String path = "somePath";
        String user = "username";
        List<Path> listExpected =  List.of(Path.of(path));
        Response response = repository.register(user, List.of(path));
        assertEquals(Status.REGISTER,response.status());
        assertEquals(user,response.username());
        assertIterableEquals(listExpected,response.filePaths());
    }

    @Test
    public void testRegisterNullUsername(){
        String path = "somePath";
        assertThrows(IllegalArgumentException.class,()->repository.register(null,List.of(path)));
    }

    @Test
    public void testRegisterBlankUsername(){
        String path = "somePath";
        assertThrows(IllegalArgumentException.class,()->repository.register("     \t\n",List.of(path)));
    }

    @Test
    public void testRegisterNullPaths(){
        assertThrows(IllegalArgumentException.class,()->repository.register("username",null));
    }

    @Test
    public void testUnregisterValidPaths(){
        String path = "somePath";
        String path1 = "someAnotherPath";
        String user = "username";
        List<Path> listExpected =  List.of(Path.of(path));
        repository.register(user, List.of(path,path1));
        Response response = repository.unregister(user,List.of(path) );
        assertEquals(Status.UNREGISTER,response.status());
        assertEquals(user,response.username());
        assertIterableEquals(listExpected,response.filePaths());
    }

    @Test
    public void testUnregisterNullUsername(){
        String path = "somePath";
        assertThrows(IllegalArgumentException.class,()->repository.unregister(null,List.of(path)));
    }

    @Test
    public void testUnregisterBlankUsername(){
        String path = "somePath";
        assertThrows(IllegalArgumentException.class,()->repository.unregister("     \t\n",List.of(path)));
    }

    @Test
    public void testUnregisterNullPaths(){
        assertThrows(IllegalArgumentException.class,()->repository.unregister("username",null));
    }

    @Test
    public void testListFilesNullUsername(){
        assertThrows(IllegalArgumentException.class,()->repository.listFiles(null));
    }

    @Test
    public void testListFilesBlankUsername(){
        assertThrows(IllegalArgumentException.class,()->repository.listFiles("     \t\n"));
    }

    @Test
    public void testExistsNullUsername(){
        assertThrows(IllegalArgumentException.class,()->repository.exists(null));
    }

    @Test
    public void testExistsBlankUsername(){
        assertThrows(IllegalArgumentException.class,()->repository.exists("     \t\n"));
    }

    @Test
    public void testUnauthorizedNullUsername(){
        assertThrows(IllegalArgumentException.class,()->repository.unauthorized(null));
    }

    @Test
    public void testUnauthorizedBlankUsername(){
        assertThrows(IllegalArgumentException.class,()->repository.unauthorized("     \t\n"));
    }
    
    



}
