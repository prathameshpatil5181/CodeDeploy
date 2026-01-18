import homepagecss from "./homepage.module.scss";
export default function HomePage() {
  return (
    <div>
      <div className={homepagecss.slogan}>
        <div>Deploy your Apps on your Own Cloud</div>
        <div className={homepagecss.text2}>In 60 seconds</div>
      </div>
    </div>
  );
}
