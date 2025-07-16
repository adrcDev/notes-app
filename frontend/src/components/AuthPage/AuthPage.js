import React from "react";
import * as authPageStylesObj from "./AuthPage.css";
import { Outlet } from "react-router";
import {ThemeContext} from "../../contexts/ThemeContext";


export default function AuthPage() {
  return (
    <>
      <AppBar />
      {/* <Outlet /> */}
    </>
  );
}

function AppBar() {
  let currentThemeInfoObj=React.useContext(ThemeContext);
  let currentTheme=currentThemeInfoObj.theme;
  let changeCurrentTheme=currentThemeInfoObj.setTheme;

  function handleClickForLightDarkThemeToggleSpan(e) {
    if(currentTheme==="light")
      changeCurrentTheme("dark");
    else
      changeCurrentTheme("light");
  }

  return (
    <header className={authPageStylesObj.appBar}>
      <div className={authPageStylesObj.appLogoAppNameWrapper}>
        <span className={authPageStylesObj.appLogo}></span>
        <span className={authPageStylesObj.appName}>Todo app</span>
      </div>
      <span className={authPageStylesObj.lightDarkThemeToggle}
      onClick={handleClickForLightDarkThemeToggleSpan}></span>
    </header>
  );
}

