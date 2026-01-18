import LogoSvg from "../../assets/LogoSvg";
import appHeadercss from "./AppHeader.module.scss";

export default function AppHeader() {
  return (
    <div className={appHeadercss.main}>
      <div className={appHeadercss.logomain}>
        <LogoSvg />
      </div>
      <div className={appHeadercss.loginButton}>
        <button type="button" className="btn btn-primary">
          Login
        </button>
      </div>
    </div>
  );
}
