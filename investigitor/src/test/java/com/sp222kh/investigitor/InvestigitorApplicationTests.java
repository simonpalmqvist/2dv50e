package com.sp222kh.investigitor;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class InvestigitorApplicationTests {

    static {
        System.setProperty("GITTORRENT_URL", "foo");
        System.setProperty("GITTORRENT_FOLDER", "bar");
    }

    @Ignore
	@Test
	public void contextLoads() {
	}

}
