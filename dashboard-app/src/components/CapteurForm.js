import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import {fetchObjetsConnecte, addCapteur, updateCapteur, fetchCapteurById } from '../services/DataServices';

const CapteurForm = () => {
    const [capteur, setCapteur] = useState({
        nom: '',
        typeMesure: '',
        uniteMesure: '',
        etat: false,
        objetConnecteId: ''
    });

    const [objetsConnecte, setObjetsConnecte] = useState([]);
    const navigate = useNavigate();
    const { id } = useParams();
    const isEditing = id !== undefined;

    useEffect(() => {
        const fetchObjets = async () => {
            try {
                const response = await fetchObjetsConnecte();
                setObjetsConnecte(response.data);
            } catch (error) {
                console.error('Error fetching Objets Connecte:', error);
            }
        };

        fetchObjets();
        if (isEditing) {
            const fetchData = async () => {
                try {
                    const response = await fetchCapteurById(id);
                    if(response.data) {
                        setCapteur(response.data);
                    } else {
                        setCapteur({ nom: '', typeMesure: '', uniteMesure: '', etat: false });
                    }
                } catch (error) {
                    console.error('Error:', error);
                    setCapteur({ nom: '', typeMesure: '', uniteMesure: '', etat: false });
                }
            };
            fetchData();
        }
    }, [id, isEditing]);

    const handleChange = (event) => {
        const { name, value, type, checked } = event.target;
        setCapteur(prevState => ({
            ...prevState,
            [name]: type === 'checkbox' ? checked : value
        }));
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        try {
            let response;
            if (isEditing) {
                response = await updateCapteur(id, capteur);
                console.log("Update response", response);
            } else {
                response = await addCapteur(capteur);
                console.log("Add response", response);
            }
            const capteurId = response.data.id;
    
            if(capteurId) {
                navigate(`/capteur/${capteurId}`);
            } else {
                console.error("Capteur ID not found in response");
            }
        } catch (error) {
            console.error('Error:', error);
        }
    };

    return (
        <div className="container mt-4">
            <h2>{isEditing ? 'Edit Capteur' : 'Add New Capteur'}</h2>
            <form onSubmit={handleSubmit} className="mt-3">
                <div className="mb-3">
                    <label htmlFor="objetConnecteId" className="form-label">Objet Connecté</label>
                    <select
                        id="objetConnecteId"
                        name="objetConnecteId"
                        className="form-select"
                        value={capteur.objetConnecteId || ''}
                        onChange={handleChange}
                        required
                    >
                        <option value="">Select an Objet Connecté</option>
                        {objetsConnecte.map((objet) => (
                            <option key={objet.id} value={objet.id}>
                                {objet.nom}
                            </option>
                        ))}
                    </select>
                </div>
                <div className="mb-3">
                    <label htmlFor="nom" className="form-label">Nom</label>
                    <input
                        type="text"
                        className="form-control"
                        id="nom"
                        name="nom"
                        value={capteur.nom || ''}
                        onChange={handleChange}
                        required
                    />
                </div>

                <div className="mb-3">
                    <label htmlFor="typeMesure" className="form-label">Type de Mesure</label>
                    <input
                        type="text"
                        className="form-control"
                        id="typeMesure"
                        name="typeMesure"
                        value={capteur.typeMesure || ''}
                        onChange={handleChange}
                        required
                    />
                </div>

                <div className="mb-3">
                    <label htmlFor="uniteMesure" className="form-label">Unité de Mesure</label>
                    <input
                        type="text"
                        className="form-control"
                        id="uniteMesure"
                        name="uniteMesure"
                        value={capteur.uniteMesure || ''}
                        onChange={handleChange}
                        required
                    />
                </div>

                <div className="mb-3 form-check">
                    <input
                        type="checkbox"
                        className="form-check-input"
                        id="etat"
                        name="etat"
                        checked={capteur.etat || false}
                        onChange={handleChange}
                    />
                    <label className="form-check-label" htmlFor="etat">État Actif</label>
                </div>

                <button type="submit" className="btn btn-primary">{isEditing ? 'Update' : 'Add'} Capteur</button>

            </form>
        </div>
    );
};

export default CapteurForm;
