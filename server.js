import express from 'express';
import router from './src/routes/index.js';
import { PORT } from './src/config/env.js';
import sequelize from './src/config/db.js';
import initAssociations from './src/models/association.model.js';

const app = express();
app.use(express.json());
app.use('/api', router);

app.get('/', (req, res) => {
  res.send('Welcome to the API');
});

app.listen(PORT, async () => {
  console.log(`Server is running on http://localhost:${PORT}`);

  try {
    await sequelize.authenticate();
    console.log('Database connected successfully.');
    initAssociations();
  } catch (err) {
    console.error('Database connection failed:', err);
  }
});