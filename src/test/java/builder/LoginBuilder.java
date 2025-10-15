package builder;

import model.Login;

public class LoginBuilder {
    private Login login;

    public LoginBuilder(){
        this.login = new Login();
        this.login.setUsuarioLogin("carlos_43@ymail.com");
        this.login.setUsuarioSenha("123456");

    }

    public LoginBuilder comUsuario(String email){
        this.login.setUsuarioLogin(email);

        return this;
    }

    public LoginBuilder comSenha(String senha){
        this.login.setUsuarioSenha(senha);

        return this;
    }

    public Login builder(){
        return this.login;
    }
}
