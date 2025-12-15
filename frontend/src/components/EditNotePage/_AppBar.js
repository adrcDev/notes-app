import * as appBarStylesObject from "./_AppBar.css";
import * as React from "react";


export default function AppBar() {

  return (
    <div className={appBarStylesObject.appBar}>
      <div className={appBarStylesObject.appLogoAndNameWrapper}>
        <span className={appBarStylesObject.appLogo}></span>
        <span className={appBarStylesObject.appName}>Notes app</span>
      </div>
    </div>
  );
}