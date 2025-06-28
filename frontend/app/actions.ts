"use server";

type response = {
  status: string;
  isSolvable: boolean;
  moves: number[][][];
};

const URL =
  process.env.NODE_ENV === "production"
    ? "https://eightpuzzle-wwpx.onrender.com/api/path"
    : "http://localhost:8080/api/path";

export const solvePuzzle = async ({ arr }: { arr: number[][] }) => {
  const res = await fetch(URL, {
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
