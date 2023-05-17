const express = require('express');

const PORT = process.env.PORT ?? 3000;
const ERROR_RATE = process.env.ERROR_RATE ?? 25;

const app = express();

app.get('/api/number', (request, response) => {
  const shouldReturnError = Math.random() < ERROR_RATE / 100;
  const randomNumber = Math.floor(Math.random() * 100) + 1;

  if (shouldReturnError) {
    response.status(500).json({ code: 500, status: 'Internal Server Error' });
  } else {
    response.status(200).json({ value: randomNumber });
  }
});

app.listen(PORT, () => {
  console.log(`API listening at http://localhost:${PORT}`);
});