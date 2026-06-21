package br.csi.politecnico.financecontrol.utils;

import br.csi.politecnico.financecontrol.dto.UserToken;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtil {

    public static UserToken getUser() {
        return (UserToken) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    public static Long getId() {
        return getUser().getId();
    }

    public static String getUuid() {
        return getUser().getUuid();
    }

    public static String getEmail() {
        return getUser().getEmail();
    }

    public static String getCpf() {
        return getUser().getCpf();
    }

    public static String getName() {
        return getUser().getName();
    }

    public static String getRole() {
        return getUser().getRole();
    }
}
