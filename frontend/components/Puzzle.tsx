"use client";

import { ChangeEvent, useState } from "react";
import Box from "./Box";
import { useMutation } from "@tanstack/react-query";
import { solvePuzzle } from "@/app/actions";
import Link from "next/link";

const SOLVED_STATE = [
  [1, 2, 3],
  [4, 5, 6],
  [7, 8, 0],
];

const Puzzle = () => {
  const [arr, setArr] = useState<number[][]>(SOLVED_STATE);
  const [editable, setEditable] = useState<boolean>(false);
  const [idx, setIdx] = useState<number>(0);

  const {
    mutate: solve,
    data,
    isPending,
  } = useMutation({
    mutationKey: ["solve"],
    mutationFn: async () => await solvePuzzle({ arr }),
    onSuccess: () => setIdx(0),
  });

  const handleChange = (i: number, j: number, num: number) => {
    setArr((prev) => {
      const curr = prev.map((row) => [...row]);
      curr[i][j] = num;
      return curr;
    });
  };

  const isInvalid = () => {
    const vis = new Array(9).fill(false);
    for (let i = 0; i < 3; i++) {
      for (let j = 0; j < 3; j++) {
        if (arr[i][j] < 0 || arr[i][j] > 8) return true;
        if (vis[arr[i][j]] === true) {
          return true;
        } else {
          vis[arr[i][j]] = true;
        }
      }
    }

    return false;
  };

  const isSolved = () => {
    for (let i = 0; i < 3; i++) {
      for (let j = 0; j < 3; j++) {
        if (SOLVED_STATE[i][j] !== arr[i][j]) return false;
      }
    }
    return true;
  };

  const handleOnClick = (i: number, j: number) => {
    let swapi = i;
    let swapj = j;
    //left
    if (j > 0 && arr[i][j - 1] === 0) {
      swapj = j - 1;
    }
    //right
    if (j < arr[0].length - 1 && arr[i][j + 1] === 0) {
      swapj = j + 1;
    }
    //top
    if (i > 0 && arr[i - 1][j] === 0) {
      swapi = i - 1;
    }
    //bottom
    if (i < arr.length - 1 && arr[i + 1][j] === 0) {
      swapi = i + 1;
    }
    //swap
    setArr((prev) => {
      const curr = prev.map((row) => [...row]);
      let temp = curr[i][j];
      curr[i][j] = curr[swapi][swapj];
      curr[swapi][swapj] = temp;
      return curr;
    });
  };

  return (
    <div className="relative flex flex-col items-center space-y-8">
      <h1 className="text-3xl font-semibold text-orange-400">8puzzle</h1>
      <Link
        href="https://github.com/sayanta-nandi/8puzzle"
        className="absolute top-0 right-0"
      >
        github
      </Link>
      <div>
        <div className="flex justify-between">
          <p
            className={`${
              isInvalid() ? "text-red-500" : isSolved() ? "text-green-500" : ""
            }`}
          >
            {isInvalid() ? "invalid" : isSolved() ? "solved" : "unsolve"}
          </p>
          <button
            onClick={() => {
              if (!editable) setEditable(true);
              else {
                if (!isInvalid()) {
                  setEditable(false);
                }
              }
            }}
          >
            {editable ? "done" : "edit"}
          </button>
        </div>
        <div
          className={`grid grid-cols-3 p-4 gap-4 border-2  ${
            isSolved() ? "border-green-500" : "border-orange-600"
          }`}
        >
          <Box
            handleChange={handleChange}
            editable={editable}
            handleOnClick={handleOnClick}
            arr={arr}
            i={0}
            j={0}
          />
          <Box
            handleChange={handleChange}
            editable={editable}
            handleOnClick={handleOnClick}
            arr={arr}
            i={0}
            j={1}
          />
          <Box
            handleChange={handleChange}
            editable={editable}
            handleOnClick={handleOnClick}
            arr={arr}
            i={0}
            j={2}
          />
          <Box
            handleChange={handleChange}
            editable={editable}
            handleOnClick={handleOnClick}
            arr={arr}
            i={1}
            j={0}
          />
          <Box
            handleChange={handleChange}
            editable={editable}
            handleOnClick={handleOnClick}
            arr={arr}
            i={1}
            j={1}
          />
          <Box
            handleChange={handleChange}
            editable={editable}
            handleOnClick={handleOnClick}
            arr={arr}
            i={1}
            j={2}
          />
          <Box
            handleChange={handleChange}
            editable={editable}
            handleOnClick={handleOnClick}
            arr={arr}
            i={2}
            j={0}
          />
          <Box
            handleChange={handleChange}
            editable={editable}
            handleOnClick={handleOnClick}
            arr={arr}
            i={2}
            j={1}
          />
          <Box
            handleChange={handleChange}
            editable={editable}
            handleOnClick={handleOnClick}
            arr={arr}
            i={2}
            j={2}
          />
        </div>
        {data ? (
          data.isSolvable ? (
            <div className="flex gap-2 items-center mt-2">
              <button
                className="px-2 bg-gray-300 rounded"
                onClick={() => {
                  if (idx > 0) {
                    setArr(data.moves[idx - 1]);
                    setIdx((prev) => prev - 1);
                  }
                }}
              >
                {"<"}
              </button>
              <div className="w-4 flex justify-center">{idx}</div>
              <button
                className="px-2 bg-gray-300 rounded"
                onClick={() => {
                  if (idx < data.moves.length - 1) {
                    setArr(data.moves[idx + 1]);
                    setIdx((prev) => prev + 1);
                  }
                }}
              >
                {">"}
              </button>
            </div>
          ) : (
            "can not be solved"
          )
        ) : null}
      </div>
      {!editable && (
        <button disabled={isPending} onClick={() => solve()}>
          {isPending ? "Solving..." : "Solve by computer"}
        </button>
      )}
    </div>
  );
};
export default Puzzle;
