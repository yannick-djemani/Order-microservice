//package com.codeimmig.OrderService.external.intercept;
//
//import org.springframework.http.HttpRequest;
//import org.springframework.http.client.ClientHttpRequestExecution;
//import org.springframework.http.client.ClientHttpRequestInterceptor;
//import org.springframework.http.client.ClientHttpResponse;
//import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
//import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
//
//import java.io.IOException;
//
//public class ResTemplateInterceptor  implements ClientHttpRequestInterceptor {
//    private OAuth2AuthorizedClientManager auth2AuthorizedClientManager;
//
//    public ResTemplateInterceptor(OAuth2AuthorizedClientManager auth2AuthorizedClientManager) {
//        this.auth2AuthorizedClientManager = auth2AuthorizedClientManager;
//    }
//
//    @Override
//    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException, IOException {
//        request.getHeaders().add("Authorization", "Bearer "+auth2AuthorizedClientManager
//                .authorize(OAuth2AuthorizeRequest
//                        .withClientRegistrationId("internal-client")
//                        .principal("internal")
//                        .build())
//                .getAccessToken().getTokenValue());
//
//        return execution.execute(request, body);
//    }
//}
