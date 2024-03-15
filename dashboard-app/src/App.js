import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import './App.css';
import ObjetConnecteForm from './components/ObjetConnecteForm';
import ObjetsConnectesPage from './components/ObjetsConnectesPage';
import DispositifsPage from './components/DispositifsPage';
import ActionneurForm from './components/ActionneurForm';
import 'bootstrap/dist/css/bootstrap.min.css';

function App() {
  return (
    <Router>
      <div className="App">
        <Routes>
          <Route path="/" element={<ObjetsConnectesPage />} />
          <Route path="/add-objet-connecte" element={<ObjetConnecteForm />} />
          <Route path="/objetsconnecte/:id" element={<ObjetConnecteForm isEditing={true} />} />
          <Route path="/objetsconnecte/:id/dispositifs" element={<DispositifsPage />} />          
          <Route path="/actionneur" element={<ActionneurForm />} />          
          <Route path="/actionneur/:id" element={<ActionneurForm isEditing={true} />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
