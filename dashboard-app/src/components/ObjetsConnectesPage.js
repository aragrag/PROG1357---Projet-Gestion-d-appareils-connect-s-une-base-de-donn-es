import React, { useEffect, useState } from 'react';
import { fetchObjetsConnecte, deleteObjetConnecte } from '../services/DataServices';
import { useNavigate } from 'react-router-dom';
import ConfirmModal from './ConfirmModal';

const ObjetsConnectesPage = () => {
  const [objets, setObjets] = useState([]);
  const navigate = useNavigate();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedIdForDeletion, setSelectedIdForDeletion] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await fetchObjetsConnecte();
        setObjets(response.data);
      } catch (error) {
        console.error('Error fetching objets connectes:', error);
      }
    };

    fetchData();
  }, []);

  const handleEdit = (id) => {
    navigate(`/objetsconnecte/${id}`);
  };

  const handleDeleteClick = (id) => {
    setSelectedIdForDeletion(id);
    setIsModalOpen(true);
  };

  const confirmDelete = async () => {
    if (selectedIdForDeletion != null) {
      try {
        await deleteObjetConnecte(selectedIdForDeletion);
        setObjets(objets.filter(objet => objet.id !== selectedIdForDeletion));
        setIsModalOpen(false);
      } catch (error) {
        console.error('Error deleting objet connecte:', error);
      }
    }
  };

  const handleAdd = () => {
    navigate('/add-objet-connecte');
  };
  
  const handleDispositif = (id) => {
    navigate(`/objetsconnecte/${id}/dispositifs`);
  };
  return (
    <div className="container mt-5">
      <h2>Objets Connectés</h2>
      <button onClick={handleAdd} className="btn btn-primary mb-3">Add New Objet Connecté</button>
      <table className="table table-striped">
        <thead className="thead-dark">
          <tr>
            <th>Nom</th>
            <th>Device ID</th>
            <th>Adresse IP</th>
            <th>État</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {objets.map(objet => (
            <tr key={objet.id}>
              <td>{objet.nom}</td>
              <td>{objet.deviceID}</td>
              <td>{objet.adresseIP}</td>
              <td>{objet.etat ? 'Actif' : 'Inactif'}</td>
              <td align="right">
                <button onClick={() => handleDispositif(objet.id)} className="btn btn-sm btn-info">Dispositifs</button>
                <button onClick={() => handleEdit(objet.id)} className="btn btn-sm btn-info">Edit</button>
                <button onClick={() => handleDeleteClick(objet.id)} className="btn btn-sm btn-danger">Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      <ConfirmModal isOpen={isModalOpen} onConfirm={confirmDelete} onCancel={() => setIsModalOpen(false)} message="Are you sure you want to delete this objet connecté?" />
    </div>
  );
};

export default ObjetsConnectesPage;
