import * as React from "react";
import * as upperMessageBarStylesObj from "./_UpperMessageBar.css";

export default function UpperMessageBar({text}) {

  return (
    <div className={upperMessageBarStylesObj.upperMessageBar}>
      {text}
    </div>
  )
  
}