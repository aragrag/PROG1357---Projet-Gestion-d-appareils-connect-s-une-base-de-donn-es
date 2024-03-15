import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { fetchActionneursByObjetConnecteId, fetchCapteursByObjetConnecteId, deleteActionneur, deleteCapteur } from '../services/DataServices';
import ConfirmModal from './ConfirmModal2';

const DispositifsPage = () => {
    const { id } = useParams();
    const [actionneurs, setActionneurs] = useState([]);
    const [capteurs, setCapteurs] = useState([]);
    const [showConfirm, setShowConfirm] = useState(false);
    const [selectedId, setSelectedId] = useState(null);
    const [selectedType, setSelectedType] = useState(null);
    const navigate = useNavigate();

    const handleEdit = (itemId, type) => {
        navigate(`/${type}/${itemId}`);
    };

    const handleDeleteClick = (itemId, type) => {
        setSelectedId(itemId);
        setSelectedType(type);
        setShowConfirm(true);
    };

    const handleDelete = async () => {
        try {
            if (selectedType === 'actionneur') {
                await deleteActionneur(selectedId);
                setActionneurs(prev => prev.filter(item => item.id !== selectedId));
            } else if (selectedType === 'capteur') {
                await deleteCapteur(selectedId);
                setCapteurs(prev => prev.filter(item => item.id !== selectedId));
            }
            setShowConfirm(false);
        } catch (error) {
            console.error('Error deleting item', error);
        }
    };

    useEffect(() => {
        const fetchData = async () => {
            try {
                const actionneursResponse = await fetchActionneursByObjetConnecteId(id);
                const capteursResponse = await fetchCapteursByObjetConnecteId(id);
                setActionneurs(actionneursResponse.data);
                setCapteurs(capteursResponse.data);
            } catch (error) {
                console.error('Error fetching dispositifs:', error);
            }
        };
        fetchData();
    }, [id]);
    const handleAddCapteur = () => {
        navigate('/capteur');
    };
    const handleAddActionneur = () => {
        navigate('/actionneur');
    };    
    return (
        <div className="container mt-5">
            <h2>Dispositifs for ObjetConnecte ID: {id}</h2>
            <div className="row">
                <div className="col-6">
                    <h3>Actionneurs</h3>
                    <button onClick={handleAddActionneur} className="btn btn-primary mb-3">Add New Actionneur</button>
                    <table className="table table-striped">
                        <thead>
                            <tr>
                                <th>Nom</th>
                                <th>Type d'Action</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {actionneurs.map(actionneur => (
                                <tr key={actionneur.id}>
                                    <td>{actionneur.nom}</td>
                                    <td>{actionneur.typeAction}</td>
                                    <td>
                                        <button onClick={() => handleEdit(actionneur.id, 'actionneur')} className="btn btn-sm btn-info">Edit</button>
                                        <button onClick={() => handleDeleteClick(actionneur.id, 'actionneur')} className="btn btn-sm btn-danger">Delete</button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
                <div className="col-6">
                    <h3>Capteurs</h3>
                    <button onClick={handleAddCapteur} className="btn btn-primary mb-3">Add New Actionneur</button>
                    <table className="table table-striped">
                        <thead>
                            <tr>
                                <th>Nom</th>
                                <th>Type de Mesure</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {capteurs.map(capteur => (
                                <tr key={capteur.id}>
                                    <td>{capteur.nom}</td>
                                    <td>{capteur.typeMesure}</td>
                                    <td>
                                        <button onClick={() => handleEdit(capteur.id, 'capteur')} className="btn btn-sm btn-info">Edit</button>
                                        <button onClick={() => handleDeleteClick(capteur.id, 'capteur')} className="btn btn-sm btn-danger">Delete</button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </div>
            <ConfirmModal
                show={showConfirm}
                onClose={() => setShowConfirm(false)}
                onConfirm={handleDelete}
                title="Confirm Delete"
                message="Are you sure you want to delete this item?"
            />
        </div>
    );
};

export default DispositifsPage;
