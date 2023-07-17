package penoles.sicose.restgen;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import penoles.sicose.restgen.model.controller.ResolverController;
import penoles.sicoseutil.resource.ResourceParent;
import penoles.sicoseutil.resource.security.SecurityUtils;

@Path("{table}")
public class MainResource extends ResourceParent<Object> {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(
            @Context UriInfo ui,
            @PathParam("table") String table,
            @QueryParam("user") String user,
            @QueryParam("pass") String pass,
            @QueryParam("token") String token) {
        MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
        String securityToken = SecurityUtils.validateSecurity(user, pass);
        securityToken = securityToken == null ? SecurityUtils.validateSecurity(token) : securityToken;
        if (securityToken != null) {
            String json = ResolverController.resolveSearch(table, queryParams);
            return Response.ok().entity(json).build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @GET
    @Path("{column}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBlob(
            @Context UriInfo ui,
            @PathParam("table") String table,
            @PathParam("column") String column,
            @QueryParam("user") String user,
            @QueryParam("pass") String pass,
            @QueryParam("token") String token) {
        return Response.ok().build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response post(
            @PathParam("table") String table,
            @QueryParam("user") String user,
            @QueryParam("pass") String pass,
            @QueryParam("token") String token,
            String content) {
        String securityToken = SecurityUtils.validateSecurity(user, pass);
        securityToken = securityToken == null ? SecurityUtils.validateSecurity(token) : securityToken;
        if (securityToken != null) {
            try {
                String result = ResolverController.resolveInsert(table, content);
                if ("success".equalsIgnoreCase(result)) {
                    return Response.ok().build();
                } else {
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
                }

            } catch (Exception e) {
                return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
            }
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response put(
            @PathParam("table") String table,
            @QueryParam("user") String user,
            @QueryParam("pass") String pass,
            @QueryParam("token") String token,
            String content) {
        String securityToken = SecurityUtils.validateSecurity(user, pass);
        securityToken = securityToken == null ? SecurityUtils.validateSecurity(token) : securityToken;
        if (securityToken != null) {
            try {
                String result = ResolverController.resolveUpdate(table, content);
                if ("success".equalsIgnoreCase(result)) {
                    return Response.ok().build();
                } else {
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
                }

            } catch (Exception e) {
                return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
            }
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

}
