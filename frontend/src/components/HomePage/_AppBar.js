import { ThemeContext } from "../../contexts/ThemeContext";
import * as appBarStylesObject from "./_AppBar.css";
import * as React from "react";


export default function AppBar() {
  let currentThemeInfoObj=React.useContext(ThemeContext);
  let currentTheme=currentThemeInfoObj.theme;
  let changeCurrentTheme=currentThemeInfoObj.setTheme;

  function handleClickForAppThemeToggleSpan(e) {
    if(currentTheme==="light")
      changeCurrentTheme("dark");
    else
      changeCurrentTheme("light");
  }

  return (
    <div className={appBarStylesObject.appBar}>
      <div className={appBarStylesObject.appLogoAndNameWrapper}>
        <span className={appBarStylesObject.appLogo}></span>
        <span className={appBarStylesObject.appName}>Notes app</span>
      </div>
      <div className={appBarStylesObject.appThemeToggleAndUserOptionsWrapper}>
        <span className={appBarStylesObject.appThemeToggle} onClick={handleClickForAppThemeToggleSpan}>  </span>
        <div className={appBarStylesObject.userOptions}>hello 'Display-Name'</div>
      </div>
    </div>
  );
}