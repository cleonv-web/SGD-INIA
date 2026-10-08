package pe.gob.onpe.tramitedoc.web.validator;

import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import pe.gob.onpe.autentica.model.Usuario;
import pe.gob.onpe.autentica.service.UsuarioService;


public class UsuarioValidator {

    private static boolean instanciated = false;
    private static UsuarioValidator usuarioValidatorInstance;

    private UsuarioService usuarioService;

    public static UsuarioValidator getInstancia(UsuarioService usuarioService){
        if(!UsuarioValidator.instanciated){
            UsuarioValidator.usuarioValidatorInstance = new UsuarioValidator(usuarioService);
            UsuarioValidator.instanciated = true;
        }
        return UsuarioValidator.usuarioValidatorInstance;
    }

    private UsuarioValidator(UsuarioService usuarioService){
        this.usuarioService = usuarioService;
    }

    public void validate(Usuario usuario, Errors errors){

        if(!StringUtils.hasLength(usuario.getCoUsuario())){
            errors.rejectValue("coUsuario", null, "Es requerido usuario.");
        }

        if( usuario.getTiIdentificacion()!=2 && !StringUtils.hasLength(usuario.getDePassword())){
            errors.rejectValue("dePassword", null, "Es requerido contraseña.");
        }
        
//        if(!StringUtils.hasLength(usuario.getCoDep())){
//            errors.rejectValue("coDep", null, "Es requerido dependencia.");
//        }        
    }
}
