import * as React from "react";
import { JwtAccessTokenContext } from "../../contexts/JwtAcessTokenContext";
import { useLocation, useNavigate } from "react-router";
import LoadingModalDialog from "./_LoadingModalDialog";
import {BackendUrlContext} from "../../contexts/BackendUrlContext";


export default function JwtAuthLogicWrapper({children}) {
  let jwtAccessTokenRef=React.useContext(JwtAccessTokenContext);
  let context_backendUrl=React.useContext(BackendUrlContext);

  let [isChildrenPropToBeRendered,setIsChildrenPropToBeRendered]=React.useState(false);
  let loadingModalDialogRef=React.useRef(null);
  let currentUrlObj=useLocation();
  let navigateFuncReactRouter=useNavigate();
  // console.log(currentUrlObj);

  React.useEffect(()=>{
    if(!isChildrenPropToBeRendered) {
      loadingModalDialogRef.current.showModal();
    }
    
    let isNotAlreadyHaveJwtAccessToken=jwtAccessTokenRef.current==="";
    if(isNotAlreadyHaveJwtAccessToken) {
      let ignoreResponse=false;
      fetch(`${context_backendUrl}/auth/v1/refresh`,{
        method: "POST",
        credentials: "include"
      }).then((response)=>{
        if(ignoreResponse)
          throw new Error("response ignored");

        if(response.status===401 || response.status===500) {
          let urlPathString=currentUrlObj.pathname;
          if(!urlPathString.startsWith("/auth")) {
            navigateFuncReactRouter("/auth/login",{replace:true});
          }
          setIsChildrenPropToBeRendered(true);
        }

        if(response.ok) {
          return response.json();
        }
      }).then((jwtAccessTokenObj)=>{
        let jwtAccessToken=jwtAccessTokenObj["jwt access token"];
        jwtAccessTokenRef.current=jwtAccessToken;
        let urlPathString=currentUrlObj.pathname;
        if(urlPathString.startsWith("/auth")) {
          navigateFuncReactRouter("/",{replace:true});
        }
        setIsChildrenPropToBeRendered(true);
      }).catch((err)=>{
        if(err.message!=="response ignored") {
          if(!isChildrenPropToBeRendered) {
          /* Can show dialog for network error */

          }

        }
      });

      return ()=>{
        ignoreResponse=true;
      }
    } 
  
    let urlPathString=currentUrlObj.pathname;
    if(urlPathString.startsWith("/auth")) {
      navigateFuncReactRouter("/",{replace:true});
    }
    setIsChildrenPropToBeRendered(true);
  },[jwtAccessTokenRef,currentUrlObj,navigateFuncReactRouter,isChildrenPropToBeRendered]);

  if(isChildrenPropToBeRendered)
    return children;
  else
    return <LoadingModalDialog ref={loadingModalDialogRef} />;
}