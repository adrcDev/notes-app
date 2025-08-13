import * as React from "react";

export let JwtAccessTokenContext=React.createContext();

export function JwtAccessTokenProvider({children}) {
  let [jwtAccessToken,setJwtAccessToken]=React.useState("");

  /* add an effect here that tries to get a jwt access token from 
  the refresh endpoint of the server. If the server doesn't send a jwt access token because the request message sent by browser doesn't contain jwt refresh token in the cookie or jwt refresh contained in cookie is expired then redirect user to the login page. Otherwise get the jwt access token from the response body and update the jwtAccessToken state variable using the setter setJwtAccessToken and no need to do any redirection as since the user has a jwt access token, they are allowed to access any path segment(page) in the website. */

  return (
    <JwtAccessTokenContext.Provider value={{jwtAccessToken,setJwtAccessToken}}>
      {children}
    </JwtAccessTokenContext.Provider>
  );
  
}
  

