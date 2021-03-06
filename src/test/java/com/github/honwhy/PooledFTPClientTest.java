package com.github.honwhy;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class PooledFTPClientTest extends BasicFTPClientTestTemplate {

    @Test
    public void test() {
        BasicFTPClientManager manager = new BasicFTPClientManager();
        manager.setHost("localhost");
        manager.setPort(getListenerPort());
        manager.setUsername(ADMIN_USERNAME);
        manager.setPassword(ADMIN_PASSWORD);
        InputStream inputStream = null;
        try {
            byte[] bytes = "hello ftp server".getBytes(StandardCharsets.UTF_8.name());
            inputStream = new ByteArrayInputStream(bytes);
            PooledFTPClient ftpClient = manager.getFTPClient();
            long ct1 = ftpClient.getCreateTimestamp();
            ftpClient.storeFile("/file1.txt",inputStream);
            ftpClient.close(); // return to pool

            ftpClient =  manager.getFTPClient(); //get from pool again
            Assert.assertEquals(ct1, ftpClient.getCreateTimestamp());
            boolean ret = ftpClient.deleteFile("/file1.txt");
            Assert.assertTrue(ret);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
        }

        try {
            manager.close();
        } catch (Exception e) {
            //e.printStackTrace();
        }

    }

    @Test
    public void testAutoclose() {
        BasicFTPClientManager manager = new BasicFTPClientManager();
        manager.setHost("localhost");
        manager.setPort(getListenerPort());
        manager.setUsername(ADMIN_USERNAME);
        manager.setPassword(ADMIN_PASSWORD);
        InputStream inputStream;
        try {
            try (PooledFTPClient ftpClient = manager.getFTPClient()){
                byte[] bytes = "hello ftp server".getBytes(StandardCharsets.UTF_8.name());
                inputStream = new ByteArrayInputStream(bytes);
                ftpClient.storeFile("/file1.txt",inputStream);
                ftpClient.deleteFile("/file1.txt");
            } catch (Exception e) {
                Assert.fail(e.getMessage());
            }
        } catch (Exception e){
            Assert.fail(e.getMessage());
        }
    }
}
