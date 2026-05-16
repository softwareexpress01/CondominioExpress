package util;

/**
 *
 * @author jesus
 */
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import saas.modelos.Compañia;

/**
 * Servlet Filter implementation class ForcedLoginFilter
 */
public class ForcedLoginFilter implements Filter {

    private String[] noForwardViewIds = null;
    private FilterConfig filterConfig;
    protected final Log log = LogFactory.getLog(getClass());
    private FilterConfig config;
    private boolean isLoggedIn;

    public ForcedLoginFilter() {
    }

    public void init(FilterConfig config) {

        this.config = config;
    }

    public static boolean checkLoginState(ServletRequest request, ServletResponse response)
            throws IOException, ServletException {

        boolean isLoggedIn = false;
        HttpSession session = ((HttpServletRequest) request).getSession(false);
        BeanUsuario managedUserBean = null;
        // If there is a BeanUsuario in the session, and it has the isLoggedIn property set to true.
        if (null != session && (null != (managedUserBean = (BeanUsuario) session.getAttribute("BeanUsuario")))) {
            if (managedUserBean.isIsLoggedIn()) {
                isLoggedIn = true;
            }
        }
        //           Enumeration<String> attributesName = session.getAttributeNames();
        //          while (attributesName.hasMoreElements()) {
        //               String name = attributesName.nextElement();
        //               System.out.println("++--- Attribut " + name + " = " + session.getAttribute(name));
        //        }

        return isLoggedIn;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        //   Throwable problem = null;
        isLoggedIn = false;
        isLoggedIn = checkLoginState(request, response);
        // If this invocation of the filter is forwardable and we are not logged in.
        if (isForwardable((HttpServletRequest) request) && !isLoggedIn) {
            String loginURI = config.getInitParameter("util.LoginURI");
            loginURI = (null != loginURI) ? loginURI : DEFAULT_LOGIN_URI;
            RequestDispatcher requestDispatcher = request.getRequestDispatcher(loginURI);
            // Force the login
            requestDispatcher.forward(request, response);
            return;
        } else {
            try {
                chain.doFilter(request, response);
            } catch (Throwable t) {
                log.error("ForcedLoginFilter : " + t.getMessage(), t);
            }
        }
    }

    /**
    PART III
     * <p>Returns true if this filter is not
     * processing the login postback
     * and the filter is not already on the
     * callstack for this request.</p>
     */
    private boolean isForwardable(HttpServletRequest request) {

        boolean onCallstack = true,
                isNoForwardViewId = false;
        String noForwardViewId = null,
                requestURI = null;
        Iterator noForwardViewIdIter = null;

        if (null == request.getAttribute("util.ForcedLoginFilter.OnStack")) {
            request.setAttribute("util.ForcedLoginFilter.OnStack", Boolean.TRUE);
            onCallstack = false;
        }

        requestURI = request.getRequestURI();
        // Se examina el request para ver si  se incrementara el contador de visitas

        if ((requestURI.equals("/CondominioExpress/") | requestURI.equals("/")) & isLoggedIn == false) {
            registrarVisita();
        }
        // Solo para ambiente de produccion, si requestURI es "/" lo deja pasar
        if (requestURI.equals("/")){
            return false;
        }
        
        noForwardViewIdIter = getNoForwardViewIds(request);
        // Iterate over the list of noForwardViewIds and bail out if
        // the current requestURI contains a match
        while (!isNoForwardViewId && noForwardViewIdIter.hasNext()) {
            noForwardViewId = (String) noForwardViewIdIter.next();
            isNoForwardViewId = (-1 != requestURI.indexOf(noForwardViewId));
        }
        if (isNoForwardViewId) {
            return false;
        }
        if (onCallstack) {
            return false;
        }
        return true;
    }

    /**
     * <p>Returns an Iterator over the contents of the
     * NoForwardViewIds init parameter, or the empty
     * iterator if that parameter is not specified.</p>
     */
    Iterator getNoForwardViewIds(HttpServletRequest request) {
        Iterator result = null;
        if (null == noForwardViewIds) {
            synchronized (this) {
                noForwardViewIds = new String[0];
                String viewIdList = config.getInitParameter("util.NoForwardViewIds");
                if (null != viewIdList) {
                    try {
                        noForwardViewIds = viewIdList.split(" ");
                    } catch (Exception e) {
                        log.error("ForcedLoginFilter : " + e.getMessage(), e);
                    }
                }
            }
        }
        final String[] viewIds = noForwardViewIds;
        result = Arrays.asList(noForwardViewIds).iterator();
        return result;
    }
    private static final String DEFAULT_LOGIN_URI = "../faces/main.xhtml";

    public void registrarVisita() {

        Compañia comp = buscarCompañia();
        Integer contador = comp.getContadorVisitas();
        contador = contador + 1;
        comp.setContadorVisitas(contador);
        actualizarNumVisitas(comp);
    }

    public Compañia buscarCompañia() {

        Compañia compañia;
        String nombQuery = "buscarCompañia";
        DaoQuery<Compañia> dataAcces = new DaoQuery<Compañia>();
        try {
            compañia = dataAcces.buscarEntidad(nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        return compañia;
    }

    public void actualizarNumVisitas(Compañia comp) {

        DaoQuery<Compañia> dataAcces = new DaoQuery<Compañia>();

        try {
            dataAcces.almacenarOModificarEntidad(comp);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

    }

    public void destroy() {
        // TODO Auto-generated method stub
    }

    //   public void init(FilterConfig arg0) throws ServletException {
    // TODO Auto-generated method stub
    //   }
    public FilterConfig getFilterConfig() {
        return filterConfig;
    }

    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }
}
