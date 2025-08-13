import * as React from "react";
import * as themesObj from "../themes.css";

export let ThemeContext=React.createContext();


export function ThemeProvider({children}) {
  let [theme,setTheme]=React.useState("");

  useLocalStorageForTheme(setTheme);
  useThemeChange(theme);
 

  return (
    <ThemeContext.Provider value={{theme,setTheme}}>
      {children}
    </ThemeContext.Provider>
  );
}

function useLocalStorageForTheme(setTheme) {
  React.useEffect(()=>{
    document.body.classList.remove(themesObj.light,themesObj.dark);
    let storedTheme=localStorage.getItem("theme");
    if(storedTheme===null) {
      document.body.classList.add(themesObj.dark);
      localStorage.setItem("theme","dark");
      setTheme("dark");
    }
    else if(storedTheme==="light") {
      document.body.classList.add(themesObj.light);
      setTheme("light");
    }
    else if(storedTheme==="dark") {
      document.body.classList.add(themesObj.dark);
      setTheme("dark");
    }
  },[setTheme]);
}

function useThemeChange(theme) {
   React.useEffect(()=>{
    if(theme==="")
      return;
    
    document.body.classList.remove(themesObj.light,themesObj.dark);
    if(theme==="light") {
      document.body.classList.add(themesObj.light);
      localStorage.setItem("theme","light");
    }
    else if(theme==="dark") {
      document.body.classList.add(themesObj.dark);
      localStorage.setItem("theme","dark");
    }
  },[theme]);
}