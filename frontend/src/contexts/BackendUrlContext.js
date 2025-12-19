import * as React from "react";

export let BackendUrlContext=React.createContext();

export function BackendUrlProvider({children}) {
  let backendUrl="http://localhost:8080";
  // let backendUrl="http://192.168.1.104:8080";

  return (
    <BackendUrlContext.Provider value={backendUrl}>
      {children}
    </BackendUrlContext.Provider>
  );
}