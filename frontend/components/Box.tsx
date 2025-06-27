"use client";

import { ChangeEvent } from "react";

const Box = ({
  i,
  j,
  arr,
  handleOnClick,
  editable,
  handleChange,
}: {
  i: number;
  j: number;
  arr: number[][];
  handleOnClick: (i: number, j: number) => void;
  editable: boolean;
  handleChange: (i: number, j: number, e: number) => void;
}) => {
  const handleLocalChange = (e: ChangeEvent<HTMLInputElement>) => {
    e.preventDefault();

    const val = Number(e.target.value);
    if (!isNaN(val)) {
      handleChange(i, j, val);
    } else {
      handleChange(i, j, 0);
    }
  };
  const isInvalid = () => {
    if (arr[i][j] < 0 || arr[i][j] > 8) return true;
    return false;
  };
  return (
    <div
      className={`rounded-2xl size-26 flex justify-center items-center text-2xl text-white select-none ${
        isInvalid()
          ? "bg-red-500"
          : editable
          ? "bg-orange-300"
          : "bg-orange-500"
      } ${arr[i][j] === 0 && !editable ? "opacity-0" : ""}`}
    >
      <button
        className={`h-full w-full ${editable && "hidden"}`}
        onClick={() => handleOnClick(i, j)}
      >
        {arr[i][j]}
      </button>
      <input
        onChange={(e) => handleLocalChange(e)}
        value={arr[i][j].toString()}
        className={`h-full w-full flex text-center ${!editable && "hidden"}`}
      ></input>
    </div>
  );
};
export default Box;
