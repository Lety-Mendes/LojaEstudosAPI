package builder;

import model.Usuario;

public class UsuarioBuilder {

    public static Usuario usuarioComumComLoginESenha(String login, String senha){
        Usuario usuario = new Usuario();
        usuario.setUsuarioLogin(login);
        usuario.setUsuarioSenha(senha);

        return usuario;
    }
}
