"use server";

type response = {
  status: string;
  isSolvable: boolean;
  moves: number[][][];
};

export const solvePuzzle = async ({ arr }: { arr: number[][] }) => {
  const res = await fetch("http://localhost:8080/api/path", {
    method: "post",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      name: arr,
    }),
  });
  const data = (await res.json()) as response;

  if (data.status !== "success") {
    throw new Error("invalid response");
  }
  return { isSolvable: data.isSolvable, moves: data.moves };
};
