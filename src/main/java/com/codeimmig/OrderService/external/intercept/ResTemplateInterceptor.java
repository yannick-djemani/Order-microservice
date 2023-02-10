package com.codeimmig.OrderService.external.intercept;

//public class ResTemplateInterceptor  implements ClientHttpRequestInterceptor {
//    private OAuth2AuthorizedClientManager auth2AuthorizedClientManager;
//
//    public ResTemplateInterceptor(OAuth2AuthorizedClientManager auth2AuthorizedClientManager) {
//        this.auth2AuthorizedClientManager = auth2AuthorizedClientManager;
//    }
//
//    @Override
//    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
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
