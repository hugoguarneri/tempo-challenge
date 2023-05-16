const express = require('express');

const PORT = process.env.PORT ?? 3000;

const app = express();

app.get('/api/number', (request, response) => {
  const randomNumber = Math.floor(Math.random() * 110) + 1;

  if (randomNumber <= 100) {
    response
      .status(200)
      .json({ value: randomNumber });
  } else {
    response
      .status(500)
      .json({ code: 500, status: 'Internal Server Error' });
  }
});

app.listen(PORT, () => {
  console.log(`API listening at http://localhost:${PORT}`);
});