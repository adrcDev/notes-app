import * as React from "react";

export let JwtAccessTokenContext=React.createContext();

export function JwtAccessTokenProvider({children}) {
  let jwtAccessTokenRef=React.useRef("");
  
  return (
    <JwtAccessTokenContext.Provider value={jwtAccessTokenRef}>
      {children}
    </JwtAccessTokenContext.Provider>
  );
  
}
  

