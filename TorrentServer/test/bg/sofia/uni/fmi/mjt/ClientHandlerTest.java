package bg.sofia.uni.fmi.mjt;

import bg.sofia.uni.fmi.mjt.server.ClientHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class ClientHandlerTest {

    private Socket socketMock;
    private ClientHandler clientHandler;

    @BeforeEach
    public void setUp() {
        socketMock = Mockito.mock(Socket.class);
        clientHandler = new ClientHandler(socketMock);
    }

    @Test
    public void testValidRegisterCommand(){
       String validCommand = "register host file1, file2";
       clientHandler.getPeerInfo().setHost("host");
       assertEquals("Registered from host : file1, file2",clientHandler.commandRegister(validCommand));
    }

    @Test
    public void testInvalidRegisterCommand(){

        String validCommand = "register host file1, file2";
        clientHandler.getPeerInfo().setHost("other");
        assertEquals("Unauthorized to modify host",clientHandler.commandRegister(validCommand));
    }

    @Test
    public void testValidUnregisterCommand(){

        String input = "register host file1, file2";
        String validCommand = "unregister host file1, file2";
        clientHandler.getPeerInfo().setHost("host");
        clientHandler.commandRegister(input);
        assertEquals("Unregister from host : file1, file2",clientHandler.commandUnregister(validCommand));

    }

    @Test
    public void testInvalidUnregisterCommand(){

        String input = "register host file1, file2";
        String validCommand = "unregister host file1, file2";
        clientHandler.getPeerInfo().setHost("other");
        clientHandler.commandRegister(input);
        assertEquals("Unauthorized to modify host",clientHandler.commandUnregister(validCommand));

    }

    @Test
    public void testValidListFilesCommand(){

        String input = "register host file1, file2";
        clientHandler.getPeerInfo().setHost("host");
        clientHandler.commandRegister(input);
        ClientHandler.clients.add(clientHandler);
        assertEquals("List files from host are: file1, file2",clientHandler.commandListFiles());
        ClientHandler.clients.remove(clientHandler);
    }

    @Test
    public void testValidUpdateCommand(){

        clientHandler.getPeerInfo().setHost("host");
        clientHandler.getPeerInfo().setIp("localhost/127.0.0.1");
        clientHandler.getPeerInfo().setPort(60065);
        ClientHandler.clients.add(clientHandler);
        assertEquals("host - localhost/127.0.0.1:60065",clientHandler.commandUpdate());
        ClientHandler.clients.remove(clientHandler);
    }

    @Test
    public void testValidPeerInfoCommand(){
        String validCommand = "peer localhost/127.0.0.1, 60379";
        clientHandler.getPeerInfo().setHost("host");
        assertEquals("HOST = host, IP = localhost/127.0.0.1, PORT = 60379",clientHandler.commandInitPeerInfo(validCommand));
    }

    @Test
    public void testUnknownCommand(){
        String expected = "unknown command";
        assertEquals(expected,clientHandler.commands("register host"));
        assertEquals(expected,clientHandler.commands("regsiter host file1, file2"));

        assertEquals(expected,clientHandler.commands("unregister host"));
        assertEquals(expected,clientHandler.commands("unregsiter host file1, file2"));

        assertEquals(expected,clientHandler.commands("unregister host"));
        assertEquals(expected,clientHandler.commands("unregsiter host file1, file2"));


        assertEquals(expected,clientHandler.commands("list files"));
        assertEquals(expected,clientHandler.commands("list_files"));

        assertEquals(expected,clientHandler.commands("peer localhost/127.0.0.160379"));

    }


}
