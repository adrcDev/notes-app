import * as React from "react";
import { JwtAccessTokenContext } from "../../contexts/JwtAcessTokenContext";
import { useLocation, useNavigate } from "react-router";
import LoadingModalDialog from "./_LoadingModalDialog";
import TextModalDialog from "./_TextModalDialog";
import {BackendUrlContext} from "../../contexts/BackendUrlContext";


export default function JwtAuthLogicWrapper({children}) {
  let jwtAccessTokenRef=React.useContext(JwtAccessTokenContext);
  let context_backendUrl=React.useContext(BackendUrlContext);

  let [isChildrenPropToBeRendered,setIsChildrenPropToBeRendered]=React.useState(false);
  let [isTextModalDialogToBeShown,setIsTextModalDialogToBeShown]=React.useState(false);
  let [textModalDialogText,setTextModalDialogText]=React.useState("");
  let currentUrlObj=useLocation();
  let navigateFuncReactRouter=useNavigate();
  // console.log(currentUrlObj);

  React.useEffect(()=>{
    let isNotAlreadyHaveJwtAccessToken=jwtAccessTokenRef.current==="";
    if(isNotAlreadyHaveJwtAccessToken) {
      let ignoreResponse=false;
      fetch(`${context_backendUrl}/auth/v1/refresh`,{
        method: "POST",
        credentials: "include"
      }).then((response)=>{
        if(ignoreResponse) {
          throw new Error("response ignored");
        }

        if(response.status===500) {
          throw new Error("500");
        }

        if(response.status===401) {
          throw new Error("401");
        }

        if(response.ok) {
          return response.json();
        }
      })
      .then((jwtAccessTokenObj)=>{
        let jwtAccessToken=jwtAccessTokenObj["jwt access token"];
        jwtAccessTokenRef.current=jwtAccessToken;
        let urlPathString=currentUrlObj.pathname;
        if(urlPathString.startsWith("/auth")) {
          navigateFuncReactRouter("/",{replace:true});
        }
        else {
          setIsChildrenPropToBeRendered(true);
        }
      })
      .catch((err)=>{
        let isNetworkError=err.message!=="response ignored" && err.message!=="500" &&
        err.message!=="401";
        if(err.message==="500") {
          setIsTextModalDialogToBeShown(true);
          setTextModalDialogText("Something went wrong. Please reload the page to try again");
          console.log("500: Internal server error!");
        }
        else if(err.message==="401") {
          let urlPathString=currentUrlObj.pathname;
          if(!urlPathString.startsWith("/auth")) {
            navigateFuncReactRouter("/auth/login",{replace:true});
          } 
          else {
            setIsChildrenPropToBeRendered(true);
          }
        }
        else if(isNetworkError) {
          setIsTextModalDialogToBeShown(true);
          setTextModalDialogText("Network error: couldn't complete the request. Please reload the page to try again.");
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
    else {
      setIsChildrenPropToBeRendered(true);
    }
  },[jwtAccessTokenRef,currentUrlObj,navigateFuncReactRouter,isChildrenPropToBeRendered,context_backendUrl]);

  if(isChildrenPropToBeRendered) {
    return children;
  }
  else {
  
    return (
      <>
        {isTextModalDialogToBeShown && <TextModalDialog text={textModalDialogText}/>}
        <LoadingModalDialog/>
      </>
    );
  }

}