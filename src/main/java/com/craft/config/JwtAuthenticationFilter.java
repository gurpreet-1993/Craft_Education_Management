package com.craft.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private Logger logger = LoggerFactory.getLogger(OncePerRequestFilter.class);
    @Autowired
    private JwtHelper jwtHelper;


    @Autowired
    private CustomUserDetailsService userDetailsService;

//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//
////        try {
////            Thread.sleep(500);
////        } catch (InterruptedException e) {
////            throw new RuntimeException(e);
////        }
//        //Authorization
//
//        String requestHeader = request.getHeader("Authorization");
//        //Bearer 2352345235sdfrsfgsdfsdf
//        logger.info(" Header :  {}", requestHeader);
//        String username = null;
//        String token = null;
//        if (requestHeader != null && requestHeader.startsWith("Bearer")) {
//            //looking good
//            token = requestHeader.substring(7);
//            try {
//
//                username = this.jwtHelper.getUsernameFromToken(token);
//
//            } catch (IllegalArgumentException e) {
//                logger.info("Illegal Argument while fetching the username !!");
//                e.printStackTrace();
//            } catch (ExpiredJwtException e) {
//                logger.info("Given jwt token is expired !!");
//                e.printStackTrace();
//            } catch (MalformedJwtException e) {
//                logger.info("Some changed has done in token !! Invalid Token");
//                e.printStackTrace();
//            } catch (Exception e) {
//                e.printStackTrace();
//
//            }
//
//
//        } else {
//            logger.info("Invalid Header Value !! ");
//        }
//
//
//        //
//        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//
//
//            //fetch user detail from username 
//            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
//            Boolean validateToken = this.jwtHelper.validateToken(token, userDetails);
//            if (validateToken) {
//
//                //set the authentication
//                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//
//
//            } else {
//                logger.info("Validation fails !!");
//            }
//
//
//        }
//
//        try {
//			filterChain.doFilter(request, response);
//		} catch (java.io.IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ServletException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//
//    }
//
//}
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
	        throws ServletException, IOException, java.io.IOException {

		String requestHeader = request.getHeader("Authorization");
	    String username = null;
	    String token = null;
	    if (requestHeader != null && requestHeader.startsWith("Bearer ")) {
	        token = requestHeader.substring(7);
	        try {
	            username = this.jwtHelper.getUsernameFromToken(token);
	        } catch (Exception e) {
	            logger.error("Error extracting username from token", e);
	        }
	    }

	    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
	        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
	        if (this.jwtHelper.validateToken(token, userDetails)) {
	            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	            SecurityContextHolder.getContext().setAuthentication(authentication);
	        }
	    }
	    filterChain.doFilter(request, response);
	}
}