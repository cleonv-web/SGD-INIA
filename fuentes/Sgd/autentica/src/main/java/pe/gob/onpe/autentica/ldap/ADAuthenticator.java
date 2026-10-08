/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.autentica.ldap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

/**
 *
 * @author ypino
 */
public class ADAuthenticator 
{
  private String domain;
private String secureLdap;
  private String ldapHost;
  private String searchBase;
   /*
  public ADAuthenticator() {
    this.domain = "pcm";
    this.ldapHost = "ldap://192.168.144.12:389";
    this.searchBase = "DC=pcm,DC=gob,DC=pe"; // YOUR SEARCH BASE IN LDAP
  }*/
 
  public ADAuthenticator(String secureLdap,String domain, String host,String Port, String dn)
  {
    this.domain = domain;
    if(secureLdap.equals("true")){ 
    this.ldapHost = "ldaps://"+host+":"+Port;
    System.out.println("ldapHost==>" + this.ldapHost);
    }
    else
    {   
        this.ldapHost = "ldap://"+host+":"+Port;
    System.out.println("ldapsHost==>" + this.ldapHost);
            }
    
    this.searchBase = dn;
  }
 
    public Map<String, Object> authenticate(String user, String pass) {
        String returnedAtts[] ={ "sn", "givenName", "name", "userPrincipalName", "displayName", "memberOf" };
        String searchFilter = "(&(objectClass=user)(sAMAccountName=" + user + "))";
         
        // Create the search controls
        SearchControls searchCtls = new SearchControls();
        searchCtls.setReturningAttributes(returnedAtts);
         
        // Specify the search scope
        searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
         
        Hashtable<String,String> env = new Hashtable<String,String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapHost);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, user + "@" + domain); 
        env.put(Context.SECURITY_CREDENTIALS, pass);
         
        LdapContext ctxGC = null;
        Map<String, Object> amap = null;
        try {
             
            // This is the actual Authentication piece. Will throw javax.naming.AuthenticationException
            // if the users password is not correct. Other exceptions may include IO (server not found) etc.
            ctxGC = new InitialLdapContext(env, null);
             
            // Now try a simple search and get some attributes as defined in returnedAtts
            NamingEnumeration<SearchResult> answer = ctxGC.search(searchBase, searchFilter, searchCtls);
            while (answer.hasMoreElements()) {
                SearchResult sr = (SearchResult) answer.next();
                Attributes attrs = sr.getAttributes();
                
                if (attrs != null) {
                    amap = new HashMap<String,Object>();
                    NamingEnumeration<?> ne = attrs.getAll();
                    while (ne.hasMore()) {
                        Attribute attr = (Attribute) ne.next();
                        if (attr.size() == 1) {
                            amap.put(attr.getID(), attr.get());
                        } else {
                            HashSet<String> s = new HashSet<String>();
                            NamingEnumeration n =  attr.getAll();
                            while (n.hasMoreElements()) {
                                s.add((String)n.nextElement());
                            }
                            amap.put(attr.getID(), s);
                        }
                    }
                    ne.close();
                }
                ctxGC.close();  // Close and clean up
                return amap;
            }
        } catch (NamingException nex) {
            nex.printStackTrace();
            String[] parts = nex.getMessage().split(",");
            // nex.printStackTrace();
            amap = new HashMap<String,Object>();
            
            if(parts[2].trim().equals("data 775")){
            amap.put("775","775"); //Usuario Bloqueado
             return amap;
            }
            
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
         
        return null;
    }
    
    
}