package vvsantos.fake.oauth.loop;

import vvsantos.fake.oauth.loop.service.JwtService;

public class MainLoopJwtTest {
    public static void main(String[] args) {
        var jwtService = new JwtService();
        var initialTime = System.currentTimeMillis();
        for (int i = 0; i < 10_000; i++) {
            var token = jwtService.getFakeToken();
            System.out.println(token);
        }
        System.out.println("Total time in ms: " + (System.currentTimeMillis() - initialTime));
    }
}
