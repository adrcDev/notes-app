import * as React from "react";
import { useRouteError } from "react-router";
import * as styles from "./ErrorPage.css";

export default function ErrorPage() {
  let errorObj=useRouteError();
  console.log(errorObj);

  return (
    <h1 className={styles.errorPage}>{errorObj.status} {errorObj.statusText} {errorObj.error.message}</h1>
  );
}