package ceng.estu.secret;

/**
 * @author reuzun
 */
public class Tokens {
    private String bot_Token;

    {
        bot_Token = System.getenv("TOKEN");
    }

    public String getBot_Token(){
        return this.bot_Token;
    }

    public Tokens(){
        //cant instantiate
    }
}
