import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { addObjetConnecte, updateObjetConnecte, fetchObjetConnecteById } from '../services/DataServices';

const ObjetConnecteForm = () => {
    const [objetConnecte, setObjetConnecte] = useState({
        nom: '',
        deviceID: '',
        adresseIP: '',
        etat: false // Assuming 'etat' is a boolean for a checkbox
    });
    const navigate = useNavigate();
    const { id } = useParams(); // Assuming you're using react-router-dom v6
    
    const isEditing = id !== undefined;

    useEffect(() => {
        if (id) {
            const fetchObjetData = async () => {
                try {
                    const response = await fetchObjetConnecteById(id);
                    setObjetConnecte(response.data);
                } catch (error) {
                    console.error('Failed to fetch objet connecte', error);
                }
            };
            fetchObjetData();
        }
    }, [id]);

    const handleChange = (event) => {
        const { name, value, type, checked } = event.target;
        setObjetConnecte(prevState => ({
            ...prevState,
            [name]: type === 'checkbox' ? checked : value
        }));
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        try {
            if (isEditing) {
                await updateObjetConnecte(id, objetConnecte);
            } else {
                await addObjetConnecte(objetConnecte);
            }
            navigate('/'); // Redirecting to the list of objets connectes after add/update
        } catch (error) {
            console.error('Error:', error);
        }
    };

    return (
        <div className="container mt-4">
            <h2>{isEditing ? 'Update' : 'Add'} Objet Connecté</h2>
            <form onSubmit={handleSubmit} className="mt-3">
                <div className="mb-3">
                    <label htmlFor="nom" className="form-label">Nom:</label>
                    <input
                        id="nom"
                        name="nom"
                        type="text"
                        className="form-control"
                        value={objetConnecte.nom}
                        onChange={handleChange}
                    />
                </div>
                <div className="mb-3">
                    <label htmlFor="deviceID" className="form-label">Device ID:</label>
                    <input
                        id="deviceID"
                        name="deviceID"
                        type="text"
                        className="form-control"
                        value={objetConnecte.deviceID || ''}
                        onChange={handleChange}
                    />
                </div>
                <div className="mb-3">
                    <label htmlFor="adresseIP" className="form-label">Adresse IP:</label>
                    <input
                        id="adresseIP"
                        name="adresseIP"
                        type="text"
                        className="form-control"
                        value={objetConnecte.adresseIP || ''}
                        onChange={handleChange}
                    />
                </div>
                <div className="mb-3 form-check">
                    <input
                        id="etat"
                        name="etat"
                        type="checkbox"
                        className="form-check-input"
                        checked={objetConnecte.etat || false}
                        onChange={handleChange}
                    />
                    <label htmlFor="etat" className="form-check-label">État</label>
                </div>
                <button type="submit" className="btn btn-primary">{isEditing ? 'Update' : 'Add'} Objet Connecté</button>
            </form>
        </div>
    );
};

export default ObjetConnecteForm;
