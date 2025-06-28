import { Metadata } from "next";

export function constructMetadata({
  title = "8puzzle",
  description = "Solve puzzle efficiently with computer",
  image = "/thumbnail.png",
  icons = "/favicon.ico",
}: {
  title?: string;
  description?: string;
  image?: string;
  icons?: string;
} = {}): Metadata {
  return {
    title,
    description,
    openGraph: {
      title,
      description,
      images: [{ url: image }],
    },
    icons,
    metadataBase: new URL("https://casecobra-tau-six.vercel.app/"),
  };
}
