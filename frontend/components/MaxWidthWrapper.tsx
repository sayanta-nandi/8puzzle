import { ReactNode } from "react";

const MaxWidthWrapper = ({ children }: { children: ReactNode }) => {
  return (
    <div className="h-full w-full px-2 sm:px-8 md:px-24 lg:px-48 py-2">
      {children}
    </div>
  );
};
export default MaxWidthWrapper;
