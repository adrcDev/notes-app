import { ThemeContext } from "../../contexts/ThemeContext";
import * as appBarStylesObject from "./_AppBar.css";
import * as React from "react";
import { JwtAccessTokenContext } from "../../contexts/JwtAcessTokenContext.js";
import { BackendUrlContext } from "../../contexts/BackendUrlContext.js";
import { useNavigate } from "react-router";
import TextDialog from "./_TextDialog.js";

export default function AppBar({parent_loadingModalDialogRef}) {
  let navigateFuncReactRouter=useNavigate();

  let currentThemeInfoObj=React.useContext(ThemeContext);
  let context_jwtAccessTokenRef=React.useContext(JwtAccessTokenContext);
  let context_backendUrl=React.useContext(BackendUrlContext);

  let currentTheme=currentThemeInfoObj.theme;
  let changeCurrentTheme=currentThemeInfoObj.setTheme;
  let [displayName,setDisplayName]=React.useState("");
  let [isTextDialogToBeShown,setIsTextDialogToBeShown]=React.useState(false);
  let [textDialogText,setTextDialogText]=React.useState("");

  React.useEffect(()=>{
    let isResponseToBeIgnored=false;
    async function getAndSetDisplayNameForAppBar() {
      parent_loadingModalDialogRef.current.showModal();
      let response;
      try {
        response=await fetch(`${context_backendUrl}/api/v1/users/me`,{
          method: "get",
          headers: {
            "Authorization": `Bearer ${context_jwtAccessTokenRef.current}`
          },
          credentials: "include"
        });
      } catch(e) {
        parent_loadingModalDialogRef.current.close();
        console.log("Network error");
        return;
      }

      if(isResponseToBeIgnored) {
        return;
      }

      if(response.status===500) {
        parent_loadingModalDialogRef.current.close();
        console.log("500: Internal server error");
        return;
      }

      if(response.status===200) {
        parent_loadingModalDialogRef.current.close();
        let userDetailsJsonParsedObj=await response.json();
        let displayName=userDetailsJsonParsedObj.displayName;
        setDisplayName(displayName);
        return;
      }

      //response status is 401 for GET /api/v1/users/me endpoint
      try {
        response=await fetch(`${context_backendUrl}/auth/v1/refresh`,{
          method: "post",
          credentials: "include" 
        });
      } catch(e) {
        parent_loadingModalDialogRef.current.close();
        console.log("Network error");
        return;
      }

      if(response.status===500) {
        parent_loadingModalDialogRef.current.close();
        console.log("500: Internal server error");
        return;
      }

      if(response.status===401) {
        context_jwtAccessTokenRef.current="";
        navigateFuncReactRouter("/auth/login",{replace:true});
        return;
      }

      //response status is 200 for /auth/v1/refresh
      let jwtAccessTokenParsedJsonObj=await response.json();
      let newJwtAccessToken=jwtAccessTokenParsedJsonObj["jwt access token"];
      context_jwtAccessTokenRef.current=newJwtAccessToken;

      try {
        response=await fetch(`${context_backendUrl}/api/v1/users/me`,{
          method: "get",
          headers: {
            "Authorization": `Bearer ${context_jwtAccessTokenRef.current}`
          },
          credentials: "include"
        });
      } catch(e) {
        parent_loadingModalDialogRef.current.close();
        console.log("Network error");
        return;
      }

      if(response.status===500) {
        parent_loadingModalDialogRef.current.close();
        console.log("500: Internal server error");
        return;
      }

      if(response.status===200) {
        parent_loadingModalDialogRef.current.close();
        let userDetailsJsonParsedObj=await response.json();
        let displayName=userDetailsJsonParsedObj.displayName;
        setDisplayName(displayName);
        return;
      }

      //response status is 401 for GET /api/v1/users/me endpoint
      parent_loadingModalDialogRef.current.close();
      console.log("Unexpected 401 response!");
      
    }

    getAndSetDisplayNameForAppBar();

    return ()=>{
      isResponseToBeIgnored=true;
    }
  },[context_backendUrl,context_jwtAccessTokenRef,navigateFuncReactRouter,parent_loadingModalDialogRef]);

  function handleClickForAppThemeToggleSpan(e) {
    if(currentTheme==="light")
      changeCurrentTheme("dark");
    else
      changeCurrentTheme("light");
  }

  function handleClickForLogoutButton(e) {
    let logoutConfirmDialogJsxObj=(
      <div>
        <p>Are you sure you want to log out?</p>
        <div className={appBarStylesObject.logoutDialogButtonsWrapper}>
          <button className={appBarStylesObject.logoutDialogButton}>Yes</button>
          <button className={appBarStylesObject.logoutDialogButton}>Cancel</button>
        </div>
      </div>
    );

    setIsTextDialogToBeShown(true);
    setTextDialogText(logoutConfirmDialogJsxObj);
  }

  

  return (
    <div className={appBarStylesObject.appBar}>
      <div className={appBarStylesObject.appLogoAndNameWrapper}>
        <span className={appBarStylesObject.appLogo}></span>
        <span className={appBarStylesObject.appName}>Notes app</span>
      </div>
      <div className={appBarStylesObject.appThemeToggleAndUserOptionsWrapper}>
        <span className={appBarStylesObject.appThemeToggle} onClick={handleClickForAppThemeToggleSpan}>  </span>
        <div className={appBarStylesObject.userDisplayName}>Hello {displayName}</div>
        <button className={appBarStylesObject.logoutButton}
          onClick={handleClickForLogoutButton}>Logout</button>
      </div>
      {isTextDialogToBeShown && 
        <TextDialog text={textDialogText} 
          parent_setIsTextDialogToBeShown={setIsTextDialogToBeShown}></TextDialog>}
    </div>
  );
}